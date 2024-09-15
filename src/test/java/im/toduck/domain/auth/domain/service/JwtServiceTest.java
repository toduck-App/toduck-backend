package im.toduck.domain.auth.domain.service;

import static im.toduck.fixtures.user.UserFixtures.*;
import static org.assertj.core.api.SoftAssertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;

import im.toduck.ServiceTest;
import im.toduck.domain.auth.presentation.dto.JwtPair;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.security.jwt.access.AccessTokenClaim;
import im.toduck.global.security.jwt.refresh.RefreshTokenClaim;
import im.toduck.infra.redis.refresh.RefreshToken;

public class JwtServiceTest extends ServiceTest {

	@Autowired
	private JwtService jwtService;

	private User user;
	private String accessToken;
	private String refreshToken;
	private LocalDateTime accessTokenExpiresAt;
	private LocalDateTime refreshTokenExpiresAt;
	private Long ttl;

	@BeforeEach
	void setUp() {
		user = testFixtureBuilder.buildUser(GENERAL_USER());
		accessToken = "mockAccessToken";
		refreshToken = "mockRefreshToken";
		accessTokenExpiresAt = LocalDateTime.of(2024, 1, 1, 0, 0);
		refreshTokenExpiresAt = LocalDateTime.of(2024, 2, 2, 0, 0);
		ttl = 3600L;
	}

	@Test
	void Jwt_토큰을_생성할_수_있다() {
		// given
		given(accessTokenProvider.generateToken(any(AccessTokenClaim.class)))
			.willReturn(accessToken);
		given(refreshTokenProvider.generateToken(any(RefreshTokenClaim.class)))
			.willReturn(refreshToken);
		given(refreshTokenProvider.getExpiryDate(anyString()))
			.willReturn(refreshTokenExpiresAt);
		doNothing().when(refreshTokenService)
			.save(any(RefreshToken.class));

		// when
		JwtPair jwtPair = jwtService.createToken(user);

		// then
		assertSoftly(softly -> {
			softly.assertThat(jwtPair.accessToken()).isEqualTo(accessToken);
			softly.assertThat(jwtPair.refreshToken()).isEqualTo(refreshToken);
		});
	}

	@Test
	void Refresh_Token을_갱신할_수_있다() {
		// given
		AccessTokenClaim jwtClaims = AccessTokenClaim.of(user.getId(), user.getRole().name());

		given(refreshTokenProvider.getJwtClaimsFromToken(refreshToken))
			.willReturn(jwtClaims);
		given(accessTokenProvider.generateToken(any(AccessTokenClaim.class)))
			.willReturn(accessToken);
		given(refreshTokenProvider.generateToken(any(RefreshTokenClaim.class)))
			.willReturn(refreshToken);
		given(refreshTokenService.refresh(eq(user.getId()), eq(refreshToken), anyString()))
			.willReturn(RefreshToken.of(user.getId(), refreshToken, ttl));

		// when
		Pair<Long, JwtPair> result = jwtService.refresh(refreshToken);

		// then
		assertSoftly(softly -> {
			softly.assertThat(result.getFirst()).isEqualTo(user.getId());
			softly.assertThat(result.getSecond().accessToken()).isEqualTo(accessToken);
			softly.assertThat(result.getSecond().refreshToken()).isEqualTo(refreshToken);
		});
	}

	@Test
	void 토큰삭제_요청시_블랙리스트에_등록할_수_있다() {
		// given
		AccessTokenClaim jwtClaims = AccessTokenClaim.of(user.getId(), user.getRole().name());

		given(refreshTokenProvider.getJwtClaimsFromToken(refreshToken))
			.willReturn(jwtClaims);
		doNothing().when(refreshTokenService)
			.delete(eq(user.getId()), eq(refreshToken));
		doNothing().when(forbiddenTokenService)
			.createForbiddenToken(eq(accessToken), eq(user.getId()), any(LocalDateTime.class));

		// when
		jwtService.removeAccessTokenAndRefreshToken(user.getId(), accessToken, refreshToken);

		// then
		assertSoftly(softly -> {
			softly.assertThatCode(
					() -> refreshTokenService.delete(user.getId(), refreshToken))
				.doesNotThrowAnyException();
			softly.assertThatCode(
					() -> forbiddenTokenService.createForbiddenToken(accessToken, user.getId(), accessTokenExpiresAt))
				.doesNotThrowAnyException();
		});
	}
}
