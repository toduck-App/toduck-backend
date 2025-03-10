package im.toduck.domain.auth.presentation.dto.request;

import static im.toduck.global.regex.UserRegex.*;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 회원가입 요청 Dto
 * <br/>
 * 일반 회원가입 시엔 General, OIDC 회원 가입 시 Oidc를 사용합니다.
 */
public class SignUpRequest {
	@Schema(description = "회원가입 요청 DTO")
	public record General(
		@Schema(description = "사용자 전화번호", example = "01012345678")
		@Pattern(regexp = PHONE_NUMBER_REGEXP, message = "올바른 전화번호를 입력해주세요.")
		@NotBlank(message = "전화번호를 입력해주세요.")
		String phoneNumber,

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

	@Schema(title = "소셜 회원가입 요청 DTO")
	public record Oidc(
		@Schema(description = "OAuth id")
		@NotBlank(message = "OAuth id는 필수 입력값입니다.")
		String oauthId,
		@Schema(description = "OIDC 토큰")
		@NotBlank(message = "OIDC 토큰은 필수 입력값입니다.")
		String idToken,
		@Schema(description = "OIDC nonce")
		@NotBlank(message = "OIDC nonce는 필수 입력값입니다.")
		String nonce
	) {
	}

}
