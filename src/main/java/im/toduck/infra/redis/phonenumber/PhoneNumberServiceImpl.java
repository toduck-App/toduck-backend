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
		verifiyCodeUtil.sendVerifyCodeMessage(VerifyCodeDto.from(phoneNumber.getPhoneNumber(), phoneNumber.getVerifyCode()));
	}

	@Override
	public void sendVerifiedCodeToPhoneNumber(String phoneNumber) {
		String verifyCode = verifiyCodeUtil.generateVerifyCode();
		phoneNumberRepository.save(PhoneNumber.from(phoneNumber, verifyCode));
		verifiyCodeUtil.sendVerifyCodeMessage(VerifyCodeDto.from(phoneNumber, verifyCode));
	}
}
