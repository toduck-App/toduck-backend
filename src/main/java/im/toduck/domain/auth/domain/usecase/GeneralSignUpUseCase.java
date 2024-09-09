package im.toduck.domain.auth.domain.usecase;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.auth.domain.service.NickNameGenerateService;
import im.toduck.domain.auth.presentation.dto.request.SignUpRequest;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.infra.redis.phonenumber.PhoneNumber;
import im.toduck.infra.redis.phonenumber.PhoneNumberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeneralSignUpUseCase {
	private final UserService userService;
	private final PhoneNumberService phoneNumberService;
	private final PasswordEncoder passwordEncoder;
	private final NickNameGenerateService nickNameGenerateService;

	public void sendVerifiedCodeToPhoneNumber(String phoneNumber) {
		userService.validateUserByPhoneNumber(phoneNumber);

		phoneNumberService.findAlreadySentPhoneNumber(phoneNumber)
			.ifPresentOrElse(phoneNumberService::reSendVerifiedCodeToPhoneNumber,
				() -> phoneNumberService.sendVerifiedCodeToPhoneNumber(phoneNumber));
	}

	public void checkVerifiedCode(String phoneNumber, String verifiedCode) {
		PhoneNumber phoneNumberEntity = phoneNumberService.findAlreadySentPhoneNumber(phoneNumber)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_SEND_PHONE_NUMBER));
		phoneNumberService.validateVerifiedCode(phoneNumberEntity, verifiedCode);
	}

	@Transactional(readOnly = true)
	public void checkLoginId(String loginId) {
		userService.validateByLoginId(loginId);
	}

	@Transactional
	public void signUp(SignUpRequest.General request) {
		userService.validateByLoginId(request.loginId());
		userService.validateUserByPhoneNumber(request.phoneNumber());
		phoneNumberService.validateVerifiedPhoneNumber(request.phoneNumber());
		String encodedPassword = passwordEncoder.encode(request.password());
		String nickName = nickNameGenerateService.generateRandomNickname();

		userService.registerUser(request, nickName, encodedPassword);
	}
}
