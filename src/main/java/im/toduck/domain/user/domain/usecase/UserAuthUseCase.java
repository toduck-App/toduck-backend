package im.toduck.domain.user.domain.usecase;

import static im.toduck.global.regex.UserRegex.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.auth.domain.service.JwtService;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.domain.user.presentation.dto.request.ChangePasswordRequest;
import im.toduck.domain.user.presentation.dto.request.VerifyLoginIdPhoneNumberRequest;
import im.toduck.domain.user.presentation.dto.response.LoginIdResponse;
import im.toduck.global.annotation.UseCase;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.infra.redis.phonenumber.PhoneNumberService;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class UserAuthUseCase {

	private final JwtService jwtService;
	private final PhoneNumberService phoneNumberService;
	private final UserService userService;
	private final PasswordEncoder passwordEncoder;

	public void signOut(Long userId, String authHeader, String refreshToken) {
		jwtService.removeAccessTokenAndRefreshToken(userId, authHeader, refreshToken);
	}

	public void sendVerifiedCodeToPhoneNumberForFind(@Pattern(regexp = PHONE_NUMBER_REGEXP) final String phoneNumber) {
		userService.findUserByPhoneNumber(phoneNumber).orElseThrow(() -> {
			log.warn("존재하지 않는 유저의 아이디 혹은 비번을 찾을려고함 phoneNumber: {}", phoneNumber);
			return CommonException.from(ExceptionCode.NOT_EXIST_PHONE_NUMBER);
		});
		phoneNumberService.findAlreadySentPhoneNumber(phoneNumber)
			.ifPresentOrElse(phoneNumberService::reSendVerifiedCodeToPhoneNumber,
				() -> phoneNumberService.sendVerifiedCodeToPhoneNumber(phoneNumber));
	}

	public LoginIdResponse findLoginId(@Pattern(regexp = PHONE_NUMBER_REGEXP) final String phoneNumber) {
		User user = userService.findUserByPhoneNumber(phoneNumber).orElseThrow(() -> {
			log.warn("존재하지 않는 유저의 아이디 혹은 비번을 찾을려고함 phoneNumber: {}", phoneNumber);
			return CommonException.from(ExceptionCode.NOT_EXIST_PHONE_NUMBER);
		});
		phoneNumberService.validateVerifiedPhoneNumber(phoneNumber);
		phoneNumberService.deleteVerifiedPhoneNumber(phoneNumber);
		log.info("로그인 id 찾기 성공 phoneNumber: {}, loginId: {}", phoneNumber, user.getLoginId());
		return LoginIdResponse.builder()
			.loginId(user.getLoginId())
			.build();
	}

	public void verifyLoginIdPhoneNumber(final VerifyLoginIdPhoneNumberRequest request) {
		User user = userService.findUserByPhoneNumber(request.phoneNumber()).orElseThrow(() -> {
			log.warn("존재하지 않는 유저의 아이디 혹은 비번을 찾을려고함 phoneNumber: {}", request.phoneNumber());
			return CommonException.from(ExceptionCode.NOT_EXIST_PHONE_NUMBER);
		});
		if (!user.getLoginId().equals(request.loginId())) {
			log.warn("로그인 id가 일치하지 않음 phoneNumber: {}, loginId: {}", request.phoneNumber(), request.loginId());
			throw CommonException.from(ExceptionCode.INVALID_LOGIN_ID);
		}
		phoneNumberService.validateVerifiedPhoneNumber(request.phoneNumber());
		log.info("로그인 id, phoneNumber 인증 성공 phoneNumber: {}, loginId: {}", request.phoneNumber(), request.loginId());
	}

	@Transactional
	public void changePassword(final ChangePasswordRequest request) {
		phoneNumberService.validateVerifiedPhoneNumber(request.phoneNumber());

		User user = userService.findUserByPhoneNumber(request.phoneNumber()).orElseThrow(() -> {
			log.warn("존재하지 않는 유저의 아이디 혹은 비번을 찾을려고함 phoneNumber: {}", request.phoneNumber());
			return CommonException.from(ExceptionCode.NOT_EXIST_PHONE_NUMBER);
		});

		if (!user.getLoginId().equals(request.loginId())) {
			log.warn("로그인 id가 일치하지 않음 phoneNumber: {}, loginId: {}", request.phoneNumber(), request.loginId());
			throw CommonException.from(ExceptionCode.INVALID_LOGIN_ID);
		}

		String encodedPassword = passwordEncoder.encode(request.changedPassword());
		user.changePassword(encodedPassword);
		phoneNumberService.deleteVerifiedPhoneNumber(request.phoneNumber());
	}
}
