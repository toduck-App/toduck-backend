package im.toduck.domain.auth.domain.usecase;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.auth.domain.service.NickNameGenerateService;
import im.toduck.domain.auth.presentation.dto.request.RegisterRequest;
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

	/**
	 * 인증코드를 보내는 메서드
	 * validate : 이미 가입된 번호인지 확인
	 * validate : 이미 인증코드를 보낸 번호인지 확인
	 * 인증코드 전송/재정송 (재전송 가능 횟수는 제한해 놓음)
	 * @param phoneNumber
	 */
	public void sendVerifiedCodeToPhoneNumber(String phoneNumber) {
		userService.validateUserByPhoneNumber(phoneNumber);

		phoneNumberService.findAlreadySentPhoneNumber(phoneNumber)
			.ifPresentOrElse(phoneNumberService::reSendVerifiedCodeToPhoneNumber,
				() -> phoneNumberService.sendVerifiedCodeToPhoneNumber(phoneNumber));
	}

	/**
	 * 인증코드 확인 메서드
	 * @param phoneNumber
	 * @param verifiedCode
	 */
	public void checkVerifiedCode(String phoneNumber, String verifiedCode) {
		PhoneNumber phoneNumberEntity = phoneNumberService.findAlreadySentPhoneNumber(phoneNumber)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_SEND_PHONE_NUMBER));
		phoneNumberService.validateVerifiedCode(phoneNumberEntity, verifiedCode);
	}

	/**
	 * userId 중복 확인 메서드
	 * @param userId
	 */
	@Transactional(readOnly = true)
	public void checkUserId(String userId) {
		userService.validateByUserId(userId);
	}

	/**
	 * 회원가입 메서드
	 * validate : userId 중복 확인
	 * validate : phoneNumber 중복 확인
	 * validate : 인증된 phoneNumber인지 확인
	 * @param request
	 */
	@Transactional
	public void signUp(RegisterRequest request) {
		userService.validateByUserId(request.userId());
		userService.validateUserByPhoneNumber(request.phoneNumber());
		phoneNumberService.validateVerifiedPhoneNumber(request.phoneNumber());
		String encodedPassword = passwordEncoder.encode(request.password());
		String nickName = nickNameGenerateService.generateRandomNickname();

		userService.registerUser(request,nickName,encodedPassword);
	}
}
