package im.toduck.global.security.jwt;

import java.time.LocalDateTime;

import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;

public interface JwtProvider {
	String TOKEN_TYPE = "Bearer ";

	/**
	 * 헤더로부터 토큰을 추출하고 유효성을 검사하는 메서드
	 *
	 * @param authHeader : 메시지 헤더
	 * @return 값이 있다면 토큰, 없다면 빈 문자열
	 */
	default String resolveToken(String authHeader) {
		if (StringUtils.hasText(authHeader) && authHeader.startsWith(TOKEN_TYPE)) {
			return authHeader.substring(TOKEN_TYPE.length());
		}
		return "";
	}

	/**
	 * 토큰을 생성하는 메서드
	 *
	 * @param subs {@link JwtClaims} : 토큰 payload에 담을 정보
	 * @return String : 토큰
	 */
	String generateToken(JwtClaims subs);

	/**
	 * 토큰으로 부터 payload를 추출하여 JwtClaims 객체로 반환하는 메서드
	 *
	 * @param token String : 토큰
	 * @return {@link JwtClaims} : 사용자 정보
	 */
	JwtClaims getJwtClaimsFromToken(String token);

	/**
	 * 토큰의 만료일을 추출하는 메서드
	 *
	 * @param token String : 토큰
	 * @return LocalDateTime : 만료일
	 */
	LocalDateTime getExpiryDate(String token);

	/**
	 * 토큰의 만료 여부를 검사하는 메서드
	 * {@code @Deprecated}
	 */
	// boolean isTokenExpired(String token);

	/**
	 * 토큰으로부터 payload 정보를 추출하는 메서드
	 *
	 * @param token String : 토큰
	 * @return Claims : 사용자 정보
	 */
	Claims getClaimsFromToken(String token);
}
