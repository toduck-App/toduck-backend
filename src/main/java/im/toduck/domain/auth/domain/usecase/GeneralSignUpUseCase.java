package im.toduck.domain.auth.domain.usecase;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.auth.domain.service.NickNameGenerateService;
import im.toduck.domain.auth.presentation.dto.request.SignUpRequest;
import im.toduck.domain.diary.domain.service.MasterKeywordService;
import im.toduck.domain.diary.domain.service.UserKeywordService;
import im.toduck.domain.diary.persistence.entity.MasterKeyword;
import im.toduck.domain.user.common.mapper.UserMapper;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.annotation.UseCase;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.infra.redis.phonenumber.PhoneNumber;
import im.toduck.infra.redis.phonenumber.PhoneNumberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@UseCase
@RequiredArgsConstructor
@Slf4j
public class GeneralSignUpUseCase {
	private final UserService userService;
	private final PhoneNumberService phoneNumberService;
	private final PasswordEncoder passwordEncoder;
	private final NickNameGenerateService nickNameGenerateService;
	private final UserKeywordService userKeywordService;
	private final MasterKeywordService masterKeywordService;

	public void sendVerifiedCodeToPhoneNumber(String phoneNumber) {
		userService.findUserByPhoneNumber(phoneNumber).ifPresent(user -> {
			throw CommonException.from(ExceptionCode.EXISTS_PHONE_NUMBER);
		});

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
		userService.findUserByPhoneNumber(request.phoneNumber()).ifPresent(user -> {
			throw CommonException.from(ExceptionCode.EXISTS_PHONE_NUMBER);
		});
		phoneNumberService.validateVerifiedPhoneNumber(request.phoneNumber());
		String encodedPassword = passwordEncoder.encode(request.password());
		String nickname = nickNameGenerateService.generateRandomNickname();

		userService.registerGeneralUser(
			UserMapper.toGeneralUser(nickname, request.loginId(), encodedPassword, request.phoneNumber()));
		phoneNumberService.deleteVerifiedPhoneNumber(request.phoneNumber());

		User newUser = userService.getUserByLoginId(request.loginId())
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		List<MasterKeyword> masterKeywords = masterKeywordService.findAll();
		userKeywordService.setupKeywordsFromMaster(newUser, masterKeywords);
	}
}
