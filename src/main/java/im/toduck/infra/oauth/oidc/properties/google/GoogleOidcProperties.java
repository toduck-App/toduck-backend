package im.toduck.infra.oauth.oidc.properties.google;

import org.springframework.boot.context.properties.ConfigurationProperties;

import im.toduck.infra.oauth.oidc.properties.OidcClientProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "oauth2.client.provider.google")
public class GoogleOidcProperties implements OidcClientProperties {
	private final String jwksUri;
	private final String secret;
	private final String issuer;
}
