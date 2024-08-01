package im.toduck.domain.auth.domain.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import im.toduck.domain.auth.presentation.dto.JwtPair;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.security.jwt.JwtClaims;
import im.toduck.global.security.jwt.access.AccessTokenClaim;
import im.toduck.global.security.jwt.access.AccessTokenProvider;
import im.toduck.global.security.jwt.refresh.RefreshTokenClaim;
import im.toduck.global.security.jwt.refresh.RefreshTokenClaimKeys;
import im.toduck.global.security.jwt.refresh.RefreshTokenProvider;
import im.toduck.infra.redis.forbidden.ForbiddenTokenService;
import im.toduck.infra.redis.refresh.RefreshToken;
import im.toduck.infra.redis.refresh.RefreshTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
	private final AccessTokenProvider accessTokenProvider;
	private final RefreshTokenProvider refreshTokenProvider;
	private final RefreshTokenService refreshTokenService;
	private final ForbiddenTokenService forbiddenTokenService;

	public JwtPair createToken(User user) {
		String accessToken = accessTokenProvider.generateToken(
			AccessTokenClaim.of(user.getId(), user.getRole().name()));
		String refreshToken = refreshTokenProvider.generateToken(
			RefreshTokenClaim.of(user.getId(), user.getRole().name()));

		refreshTokenService.save(
			RefreshToken.of(user.getId(), refreshToken, toSeconds(refreshTokenProvider.getExpiryDate(refreshToken))));
		return JwtPair.of(accessToken, refreshToken);
	}

	public Pair<Long, JwtPair> refresh(String refreshToken) {
		Map<String, ?> claims;
		try {
			claims = refreshTokenProvider.getJwtClaimsFromToken(refreshToken).getClaims();
		} catch (ExpiredJwtException e) {
			throw CommonException.from(ExceptionCode.EXPIRED_REFRESH_TOKEN);
		}

		Long userId = Long.parseLong((String)claims.get(RefreshTokenClaimKeys.USER_ID.getValue()));
		String role = (String)claims.get(RefreshTokenClaimKeys.ROLE.getValue());

		String newAccessToken = accessTokenProvider.generateToken(AccessTokenClaim.of(userId, role));
		RefreshToken newRefreshToken;

		try {
			newRefreshToken = refreshTokenService.refresh(userId, refreshToken,
				refreshTokenProvider.generateToken(RefreshTokenClaim.of(userId, role)));
		} catch (IllegalArgumentException e) {
			throw CommonException.from(ExceptionCode.EXPIRED_REFRESH_TOKEN);
		} catch (IllegalStateException e) {
			throw CommonException.from(ExceptionCode.TAKEN_AWAY_TOKEN);
		}

		return Pair.of(userId, JwtPair.of(newAccessToken, newRefreshToken.getToken()));
	}

	public void removeAccessTokenAndRefreshToken(Long userId, String accessToken, String refreshToken) {
		JwtClaims jwtClaims = null;
		if (refreshToken != null) {
			try {
				jwtClaims = refreshTokenProvider.getJwtClaimsFromToken(refreshToken);
			} catch (JwtException ex) {
				if (!(ex instanceof ExpiredJwtException)) {
					throw ex;
				}
			}
		}

		if (jwtClaims != null) {
			deleteRefreshToken(userId, jwtClaims, refreshToken);
		}

		deleteAccessToken(userId, accessToken);
	}

	private void deleteRefreshToken(Long userId, JwtClaims jwtClaims, String refreshToken) {
		Long refreshTokenUserId = Long.parseLong(
			(String)jwtClaims.getClaims().get(RefreshTokenClaimKeys.USER_ID.getValue()));

		if (!userId.equals(refreshTokenUserId)) {
			log.warn("소유권이 없는 RT에 대한 삭제 요청 . userId : {}", userId);
			throw CommonException.from(ExceptionCode.TAKEN_AWAY_TOKEN);
		}

		try {
			refreshTokenService.delete(refreshTokenUserId, refreshToken);
		} catch (IllegalArgumentException e) {
			log.warn("refresh token을 찾을 수 없음. userId : {}", userId);
		}
	}

	private void deleteAccessToken(Long userId, String accessToken) {
		LocalDateTime expiresAt = accessTokenProvider.getExpiryDate(accessToken);
		forbiddenTokenService.createForbiddenToken(accessToken, userId, expiresAt);
	}

	private long toSeconds(LocalDateTime expiryTime) {
		return Duration.between(LocalDateTime.now(), expiryTime).getSeconds();
	}
}
