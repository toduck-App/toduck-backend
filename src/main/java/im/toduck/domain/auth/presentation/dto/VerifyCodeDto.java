package im.toduck.domain.auth.presentation.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class VerifyCodeDto {
	private String phoneNumber;
	private String code;
	private String time;

	private VerifyCodeDto(String phoneNumber, String code) {
		this.phoneNumber = phoneNumber;
		this.code = code;
		this.time = String.valueOf(System.currentTimeMillis());
	}

	public static VerifyCodeDto of(String phoneNumber, String verifyCode) {
		return new VerifyCodeDto(phoneNumber, verifyCode);
	}
}
