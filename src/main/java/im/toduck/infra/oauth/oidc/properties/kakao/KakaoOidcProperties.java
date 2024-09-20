package im.toduck.infra.oauth.oidc.properties.kakao;

import org.springframework.boot.context.properties.ConfigurationProperties;

import im.toduck.infra.oauth.oidc.properties.OidcClientProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "oauth2.client.provider.kakao")
public class KakaoOidcProperties implements OidcClientProperties {
	private final String jwksUri;
	private final String secret;

	@Override
	public String getIssuer() {
		return jwksUri;
	}
}

