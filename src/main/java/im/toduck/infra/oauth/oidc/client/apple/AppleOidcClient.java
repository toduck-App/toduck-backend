package im.toduck.infra.oauth.oidc.client.apple;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import im.toduck.infra.oauth.oidc.client.OidcClient;
import im.toduck.infra.oauth.oidc.dto.OidcPublicKeyResponse;

@FeignClient(
	name = "AppleOauthClient",
	url = "${oauth2.client.provider.apple.jwks-uri}",
	qualifiers = "appleOauthClient"
)
public interface AppleOidcClient extends OidcClient {
	@Override
	@GetMapping("/auth/keys")
	OidcPublicKeyResponse getOidcPublicKey();
}
