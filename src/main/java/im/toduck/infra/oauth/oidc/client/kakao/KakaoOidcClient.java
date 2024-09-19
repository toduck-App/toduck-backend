package im.toduck.infra.oauth.oidc.client.kakao;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import im.toduck.infra.oauth.oidc.client.OidcClient;
import im.toduck.infra.oauth.oidc.dto.OidcPublicKeyResponse;

@FeignClient(
	name = "KakaoOidcClient",
	url = "${oauth2.client.provider.kakao.jwks-uri}",
	qualifiers = "kakaoOidcClient"
)
public interface KakaoOidcClient extends OidcClient {
	@Override
	@Cacheable(value = "KakaoOauth", cacheManager = "oidcCacheManager")
	@GetMapping("/.well-known/jwks.json")
	OidcPublicKeyResponse getOidcPublicKey();
}
