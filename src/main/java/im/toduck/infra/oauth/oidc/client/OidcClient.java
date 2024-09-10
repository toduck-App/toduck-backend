package im.toduck.infra.oauth.oidc.client;

import im.toduck.infra.oauth.oidc.dto.OidcPublicKeyResponse;

public interface OidcClient {
	OidcPublicKeyResponse getOidcPublicKey();
}
