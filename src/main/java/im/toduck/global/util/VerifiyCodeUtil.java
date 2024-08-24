package im.toduck.global.util;

import im.toduck.domain.auth.presentation.dto.VerifyCodeDto;

public interface VerifiyCodeUtil {
	default String generateVerifyCode() {
		int randomNumber = (int) (Math.random() * 100000); // 0부터 99999 사이의 숫자 생성
		return String.format("%05d", randomNumber); // 5자리로 포맷팅
	}

	void sendVerifyCodeMessage(VerifyCodeDto verifyCodeDto);
}
