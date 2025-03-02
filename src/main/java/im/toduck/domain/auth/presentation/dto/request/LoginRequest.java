package im.toduck.domain.auth.presentation.dto.request;

import static im.toduck.global.regex.UserRegex.*;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "로그인 요청 DTO")
public record LoginRequest(

	@Schema(description = "사용자 아이디", example = "toduck")
	@Pattern(regexp = LOGIN_ID_REGEXP, message = "올바른 ID를 입력해주세요.")
	@NotBlank(message = "아이디를 입력해주세요.")
	String loginId,

	@Schema(description = "사용자 비밀번호", example = "Password2025@")
	@Pattern(regexp = PASSWORD_REGEXP, message = "올바른 비밀번호를 입력해주세요.")
	@NotBlank(message = "비밀번호를 입력해주세요.")
	String password
) {
}
