package im.toduck.infra.oauth.oidc.dto;

public record OidcPayload(
	/* issuer */
	String iss,
	/* client id */
	String aud,
	/* aouth provider account unique id */
	String sub,
	String email
) {
}
