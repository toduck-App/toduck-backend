package im.toduck.domain.user.common.mapper;

import im.toduck.domain.user.persistence.entity.OAuthProvider;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.domain.user.persistence.entity.UserRole;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class UserMapper {
	public static User createGeneralUser(String nickname, String loginId, String password, String phoneNumber) {
		return User.builder()
			.role(UserRole.USER)
			.nickname(nickname)
			.loginId(loginId)
			.password(password)
			.phoneNumber(phoneNumber)
			.build();
	}

	public static User createOAuthUser(final String nickname, final OAuthProvider provider, final String email) {
		return User.builder()
			.role(UserRole.USER)
			.nickname(nickname)
			.provider(provider)
			.email(email)
			.build();
	}
}
