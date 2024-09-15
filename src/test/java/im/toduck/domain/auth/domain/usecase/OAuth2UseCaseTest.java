package im.toduck.domain.auth.domain.usecase;

import static im.toduck.fixtures.user.UserFixtures.*;
import static org.assertj.core.api.SoftAssertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;

import im.toduck.UseCaseTest;
import im.toduck.domain.auth.presentation.dto.JwtPair;
import im.toduck.domain.auth.presentation.dto.request.SignUpRequest;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.infra.oauth.OidcProvider;
import im.toduck.infra.oauth.oidc.dto.OidcPayload;

class OAuth2UseCaseTest extends UseCaseTest {

	@Autowired
	private OAuth2UseCase oAuth2UseCase;

	private User buildOAuthUserEntity;
	private SignUpRequest.Oidc signUpRequest;
	private OidcProvider requestProvider;
	private OidcPayload oidcPayload;
	private String randomNickname;
	private String accessToken;
	private String refreshToken;
	private JwtPair jwtPair;

	@BeforeEach
	void setUp() {
		buildOAuthUserEntity = testFixtureBuilder.buildUser(OAUTH_USER());
		signUpRequest = new SignUpRequest.Oidc("oauthId", "idToken", "nonce");
		requestProvider = OidcProvider.APPLE;
		oidcPayload = new OidcPayload("issuer", "clientId", "sub", "test@example.com");
		randomNickname = buildOAuthUserEntity.getNickname();
		accessToken = "mockAccessToken";
		refreshToken = "mockRefreshToken";
		jwtPair = JwtPair.of(accessToken, refreshToken);
	}

	@Nested
	@DisplayName("OIDC 토큰에서 payload를 추출하고 사용자 등록 혹은 로그인을 할 수 있다")
	class OIDC_signUpTest {

		@Test
		void ID토큰을_성공적으로_추출하고_신규회원_회원가입_후_JWT토큰을_반환한다() {
			// given
			given(oauthOidcHelper.getPayload(any(), any(), any(), any()))
				.willReturn(oidcPayload);
			given(oauthOidcHelper.getPayload(any(), anyString(), anyString(), anyString()))
				.willReturn(oidcPayload);
			given(userService.findByProviderAndEmail(any(), anyString()))
				.willReturn(Optional.empty());
			given(nickNameGenerateService.generateRandomNickname())
				.willReturn(randomNickname);
			given(userService.registerOAuthUser(any())).willReturn(buildOAuthUserEntity);
			given(jwtService.createToken(any())).willReturn(jwtPair);

			// when
			Pair<Long, JwtPair> result = oAuth2UseCase.signUp(requestProvider, signUpRequest);

			// then
			assertSoftly(softly -> {
				softly.assertThat(result.getFirst()).isEqualTo(buildOAuthUserEntity.getId());
				softly.assertThat(result.getSecond()).isEqualTo(jwtPair);
			});
		}

		@Test
		void ID토큰을_성공적으로_추출하고_기존회원_로그인_후_JWT토큰을_반환한다() {
			// given
			given(oauthOidcHelper.getPayload(any(), any(), any(), any()))
				.willReturn(oidcPayload);
			given(oauthOidcHelper.getPayload(any(), anyString(), anyString(), anyString()))
				.willReturn(oidcPayload);
			given(userService.findByProviderAndEmail(any(), anyString()))
				.willReturn(Optional.of(buildOAuthUserEntity));
			given(jwtService.createToken(any())).willReturn(jwtPair);

			// when
			Pair<Long, JwtPair> result = oAuth2UseCase.signUp(requestProvider, signUpRequest);

			// then
			assertSoftly(softly -> {
				softly.assertThat(result.getFirst()).isEqualTo(buildOAuthUserEntity.getId());
				softly.assertThat(result.getSecond()).isEqualTo(jwtPair);
			});
		}

	}
}
