package im.toduck.domain.auth.common.mapper;

import im.toduck.domain.user.persistence.entity.OAuthProvider;
import im.toduck.global.exception.VoException;
import im.toduck.global.oauth.OidcProvider;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OAuthMapper {
	public static OAuthProvider fromOidcProvider(OidcProvider oidcProvider) {
		switch (oidcProvider) {
			case KAKAO:
				return OAuthProvider.KAKAO;
			case GOOGLE:
				return OAuthProvider.GOOGLE;
			case APPLE:
				return OAuthProvider.APPLE;
			default:
				throw new VoException("Unknown OidcProvider : " + oidcProvider);
		}
	}
}
