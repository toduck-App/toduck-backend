package im.toduck.domain.auth.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 응답 DTO")
public record LoginResponse(
	@Schema(description = "인증된 사용자의 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
	String accessToken,

	@Schema(description = "인증된 사용자의 고유 ID", example = "1")
	Long userId
) {
	public static LoginResponse of(String accessToken, Long userId) {
		return new LoginResponse(accessToken, userId);
	}
}
