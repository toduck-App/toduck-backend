package im.toduck.infra.oauth.oidc.client.kakao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import im.toduck.infra.oauth.oidc.client.OidcClient;
import im.toduck.infra.oauth.oidc.dto.OidcPublicKeyResponse;

@FeignClient(
	name = "KakaoClient",
	url = "${oauth2.client.provider.google.jwks-uri}",
	qualifiers = "kakaoClient"
)
public interface KakaoOidcClient extends OidcClient {
	@Override
	@GetMapping("/.well-known/jwks.json")
	OidcPublicKeyResponse getOidcPublicKey(); //TODO : 캐싱을 통해 성능 향상 고려해볼 필요 있음
}
