package im.toduck.domain.auth.common.mapper;

import im.toduck.domain.user.persistence.entity.OAuthProvider;
import im.toduck.global.exception.VoException;
import im.toduck.infra.oauth.OidcProvider;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OAuthMapper {
	public static OAuthProvider fromOidcProvider(OidcProvider oidcProvider) {
		return switch (oidcProvider) {
			case KAKAO -> OAuthProvider.KAKAO;
			case GOOGLE -> OAuthProvider.GOOGLE;
			case APPLE -> OAuthProvider.APPLE;
			default -> throw new VoException("Unknown OidcProvider : " + oidcProvider);
		};
	}
}
