package im.toduck.infra.oauth.oidc.properties;

public interface OidcClientProperties {
	String getJwksUri();

	String getSecret();

	String getIssuer();
}
