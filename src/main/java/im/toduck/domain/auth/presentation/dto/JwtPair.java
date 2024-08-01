package im.toduck.domain.auth.presentation.dto;

public record JwtPair(
	String accessToken,
	String refreshToken
) {
	public static JwtPair of(String accessToken, String refreshToken) {
		return new JwtPair(accessToken, refreshToken);
	}
}

