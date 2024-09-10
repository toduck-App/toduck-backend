package im.toduck.domain.auth.common.helper;

import java.util.Map;

import org.springframework.stereotype.Service;

import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.infra.oauth.OidcProvider;
import im.toduck.infra.oauth.oidc.client.OidcClient;
import im.toduck.infra.oauth.oidc.client.apple.AppleOidcClient;
import im.toduck.infra.oauth.oidc.client.google.GoogleOidcClient;
import im.toduck.infra.oauth.oidc.client.kakao.KakaoOidcClient;
import im.toduck.infra.oauth.oidc.dto.OidcPayload;
import im.toduck.infra.oauth.oidc.dto.OidcPublicKey;
import im.toduck.infra.oauth.oidc.dto.OidcPublicKeyResponse;
import im.toduck.infra.oauth.oidc.properties.OidcClientProperties;
import im.toduck.infra.oauth.oidc.properties.apple.AppleOidcProperties;
import im.toduck.infra.oauth.oidc.properties.google.GoogleOidcProperties;
import im.toduck.infra.oauth.oidc.properties.kakao.KakaoOidcProperties;
import im.toduck.infra.oauth.oidc.provider.JwtOidcProvider;

@Service
public class OAuthOidcHelper {
	private final JwtOidcProvider jwtOidcProvider;
	private final Map<OidcProvider, Map<OidcClient, OidcClientProperties>> oauthOidcClients;

	public OAuthOidcHelper(
		JwtOidcProvider jwtOidcProvider,
		KakaoOidcClient kakaoOidcClient,
		GoogleOidcClient googleOidcClient,
		AppleOidcClient appleOidcClient,
		KakaoOidcProperties kakaoOidcProperties,
		GoogleOidcProperties googleOidcProperties,
		AppleOidcProperties appleOidcProperties
	) {
		this.jwtOidcProvider = jwtOidcProvider;
		this.oauthOidcClients = Map.of(
			OidcProvider.KAKAO, Map.of(kakaoOidcClient, kakaoOidcProperties),
			OidcProvider.GOOGLE, Map.of(googleOidcClient, googleOidcProperties),
			OidcProvider.APPLE, Map.of(appleOidcClient, appleOidcProperties)
		);
	}

	/**
	 * Provider에 따라 Client와 Properties를 선택하고 Odic public key 정보를 가져와서 ID Token의 payload를 추출하는 메서드
	 *
	 * @param provider : {@link OidcProvider}
	 * @param oauthId  : Provider에서 발급한 사용자 식별자
	 * @param idToken  : idToken
	 * @param nonce    : 인증 서버 로그인 요청 시 전달한 임의의 문자열
	 * @return OIDCDecodePayload : ID Token의 payload
	 */
	public OidcPayload getPayload(OidcProvider provider, String oauthId, String idToken, String nonce) {
		OidcClient client = oauthOidcClients.get(provider).keySet().iterator().next();
		OidcClientProperties properties = oauthOidcClients.get(provider).values().iterator().next();
		OidcPublicKeyResponse response = client.getOidcPublicKey();
		return getPayloadFromIdToken(idToken, properties.getIssuer(), oauthId, properties.getSecret(), nonce, response);
	}

	/**
	 * ID Token의 payload를 추출하는 메서드 <br/>
	 * OAuth 2.0 spec에 따라 ID Token의 유효성 검사 수행 <br/>
	 *
	 * @param idToken  : idToken
	 * @param iss      : ID Token을 발급한 provider의 URL
	 * @param sub      : ID Token의 subject (사용자 식별자)
	 * @param aud      : ID Token이 발급된 앱의 앱 키
	 * @param nonce    : 인증 서버 로그인 요청 시 전달한 임의의 문자열 (Optional, 현재는 사용하지 않음)
	 * @param response : 공개키 목록
	 * @return OidcPayload : ID Token의 payload
	 */
	private OidcPayload getPayloadFromIdToken(String idToken, String iss, String sub, String aud, String nonce,
		OidcPublicKeyResponse response) {
		String kid = jwtOidcProvider.getKidFromUnsignedTokenHeader(idToken, iss, sub, aud, nonce);

		OidcPublicKey key = response.getKeys().stream()
			.filter(k -> k.kid().equals(kid))
			.findFirst()
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_MATCHED_PUBLIC_KEY));
		return jwtOidcProvider.getOidcTokenBody(idToken, key.n(), key.e());
	}
}
