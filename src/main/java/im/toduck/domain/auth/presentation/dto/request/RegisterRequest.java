package im.toduck.domain.auth.presentation.dto.request;

import static im.toduck.global.regex.UserRegex.*;

import im.toduck.global.regex.UserRegex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "회원가입 요청 DTO")
public record RegisterRequest(
	@Schema(description = "사용자 전화번호", example = "01012345678")
	@Pattern(regexp = PHONE_NUMBER_REGEXP, message = "올바른 전화번호를 입력해주세요.")
	@NotBlank(message = "전화번호를 입력해주세요.")
	String phoneNumber,

	@Schema(description = "사용자 아이디", example = "toduck")
	@Pattern(regexp = USER_ID_REGEXP, message = "올바른 ID를 입력해주세요.")
	@NotBlank(message = "아이디를 입력해주세요.")
	String userId,

	@Schema(description = "사용자 비밀번호", example = "password123")
	@Pattern(regexp = PASSWORD_REGEXP, message = "올바른 비밀번호를 입력해주세요.")
	@NotBlank(message = "비밀번호를 입력해주세요.")
	String password
) {
}
