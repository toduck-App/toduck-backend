package im.toduck.domain.auth.domain.usecase;

import org.springframework.stereotype.Service;

import im.toduck.domain.user.domain.service.UserService;
import im.toduck.infra.redis.phonenumber.PhoneNumberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeneralSignUpUseCase {
	private final UserService userService;
	private final PhoneNumberService phoneNumberService;

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
}
