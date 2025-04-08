package im.toduck.infra.sms;

import im.toduck.domain.auth.presentation.dto.VerifyCodeDto;

public abstract class VerifiyCodeUtil {
	public String generateVerifyCode() {
		int randomNumber = (int)(Math.random() * 1000000); // 0부터 99999 사이의 숫자 생성
		return String.format("%06d", randomNumber); // 5자리로 포맷팅
	}

	public abstract void sendVerifyCodeMessage(VerifyCodeDto verifyCodeDto);
}
