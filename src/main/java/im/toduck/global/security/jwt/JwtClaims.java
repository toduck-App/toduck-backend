package im.toduck.global.security.jwt;

import java.util.Map;

public interface JwtClaims {
	Map<String, ?> getClaims();
}

