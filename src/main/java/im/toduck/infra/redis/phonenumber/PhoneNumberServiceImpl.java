package im.toduck.infra.redis.phonenumber;

import java.util.Optional;

import org.springframework.stereotype.Service;

import im.toduck.domain.auth.presentation.dto.VerifyCodeDto;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.util.VerifiyCodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PhoneNumberServiceImpl implements PhoneNumberService {
	private final VerifiyCodeUtil verifiyCodeUtil;
	private final PhoneNumberRepository phoneNumberRepository;

	@Override
	public Optional<PhoneNumber> findAlreadySentPhoneNumber(String phoneNumber) {
		return phoneNumberRepository.findByPhoneNumber(phoneNumber);
	}

	@Override
	public void reSendVerifiedCodeToPhoneNumber(PhoneNumber phoneNumber) {
		if (phoneNumber.isMaxMessageCount()) {
			throw CommonException.from(ExceptionCode.OVER_MAX_MESSAGE_COUNT);
		}
		phoneNumber.countMessageCount();
		phoneNumber.reSetVerifyCode(verifiyCodeUtil.generateVerifyCode());
		phoneNumberRepository.save(phoneNumber);
		verifiyCodeUtil.sendVerifyCodeMessage(
			VerifyCodeDto.from(phoneNumber.getPhoneNumber(), phoneNumber.getVerifyCode()));
	}

	@Override
	public void sendVerifiedCodeToPhoneNumber(String phoneNumber) {
		String verifyCode = verifiyCodeUtil.generateVerifyCode();
		phoneNumberRepository.save(PhoneNumber.from(phoneNumber, verifyCode));
		verifiyCodeUtil.sendVerifyCodeMessage(VerifyCodeDto.from(phoneNumber, verifyCode));
	}

	@Override
	public void validateVerifiedCode(PhoneNumber phoneNumberEntity, String verifiedCodeRequest) {
		if (phoneNumberEntity.isMaxVerifyCount()) {
			throw CommonException.from(ExceptionCode.OVER_MAX_VERIFIED_COUNT);
		}
		phoneNumberEntity.countVerifyCount();
		if (!phoneNumberEntity.isVerifiedCode(verifiedCodeRequest)) {
			throw CommonException.from(ExceptionCode.INVALID_VERIFIED_CODE);
		}
		phoneNumberEntity.changeTrueVerify();
		phoneNumberRepository.save(phoneNumberEntity);
	}

	@Override
	public void validateVerifiedPhoneNumber(String phoneNumber) {
		PhoneNumber phoneNumberEntity = phoneNumberRepository.findByPhoneNumber(phoneNumber)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_SEND_PHONE_NUMBER));
		if (!phoneNumberEntity.isVerified()) {
			throw CommonException.from(ExceptionCode.NOT_VERIFIED_PHONE_NUMBER);
		}
	}
}
