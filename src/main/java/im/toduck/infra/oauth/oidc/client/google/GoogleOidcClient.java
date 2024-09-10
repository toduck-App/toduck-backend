package im.toduck.infra.oauth.oidc.client.google;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import im.toduck.infra.oauth.oidc.client.OidcClient;
import im.toduck.infra.oauth.oidc.dto.OidcPublicKeyResponse;

@FeignClient(
	name = "GoogleClient",
	url = "${oauth2.client.provider.google.jwks-uri}",
	qualifiers = "googleClient"
)
public interface GoogleOidcClient extends OidcClient {
	@Override
	@GetMapping("/oauth2/v3/certs")
	OidcPublicKeyResponse getOidcPublicKey();
}
