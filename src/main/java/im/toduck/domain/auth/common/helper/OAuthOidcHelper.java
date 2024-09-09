package im.toduck.domain.auth.common.helper;

import org.springframework.stereotype.Service;

import im.toduck.global.oauth.OidcProvider;
import im.toduck.global.oauth.oidc.OidcPayload;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthOidcHelper {
	public OidcPayload getPayload(OidcProvider provider, String oauthId, String idToken, String nonce) {
		return null;
	}
}
