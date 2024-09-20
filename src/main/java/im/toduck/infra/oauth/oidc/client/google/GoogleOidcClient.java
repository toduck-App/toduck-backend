package im.toduck.infra.oauth.oidc.client.google;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import im.toduck.infra.oauth.oidc.client.OidcClient;
import im.toduck.infra.oauth.oidc.dto.OidcPublicKeyResponse;

@FeignClient(
	name = "GoogleOidcClient",
	url = "${oauth2.client.provider.google.jwks-uri}",
	qualifiers = "googleOidcClient"
)
public interface GoogleOidcClient extends OidcClient {
	@Override
	@Cacheable(value = "GoogleOauth", cacheManager = "oidcCacheManager")
	@GetMapping("/oauth2/v3/certs")
	OidcPublicKeyResponse getOidcPublicKey();
}
