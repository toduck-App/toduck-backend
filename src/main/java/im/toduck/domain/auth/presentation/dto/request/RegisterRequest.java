package im.toduck.domain.auth.presentation.dto.request;

import static im.toduck.global.regex.UserRegex.*;

import im.toduck.global.regex.UserRegex;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
	@Pattern(regexp = PHONE_NUMBER_REGEXP, message = "올바른 전화번호를 입력해주세요.")
	@NotBlank(message = "전화번호를 입력해주세요.")
	String phoneNumber,
	@Pattern(regexp = USER_ID_REGEXP, message = "올바른 ID를 입력해주세요.")
	@NotBlank(message = "아이디를 입력해주세요.")
	String userId,
	@Pattern(regexp = PASSWORD_REGEXP, message = "올바른 비밀번호를 입력해주세요.")
	@NotBlank(message = "비밀번호를 입력해주세요.")
	String password
) {
}
