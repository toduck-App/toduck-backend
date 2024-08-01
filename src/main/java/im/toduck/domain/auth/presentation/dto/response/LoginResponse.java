package im.toduck.domain.auth.presentation.dto.response;

public record LoginResponse(
	String accessToken,
	Long userId
) {
	public static LoginResponse of(String accessToken, Long userId) {
		return new LoginResponse(accessToken, userId);
	}
}
