package im.toduck.global.security.jwt.refresh;

import java.util.Map;

import im.toduck.global.security.jwt.JwtClaims;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RefreshTokenClaim implements JwtClaims {
	private final Map<String, ?> claims;

	public static RefreshTokenClaim of(Long userId, String role) {
		Map<String, Object> claims = Map.of(
			RefreshTokenClaimKeys.USER_ID.getValue(), userId.toString(),
			RefreshTokenClaimKeys.ROLE.getValue(), role
		);
		return new RefreshTokenClaim(claims);
	}

	@Override
	public Map<String, ?> getClaims() {
		return claims;
	}
}
