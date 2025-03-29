package im.toduck.fixtures.user;

import java.util.UUID;

import im.toduck.domain.user.common.mapper.UserMapper;
import im.toduck.domain.user.persistence.entity.OAuthProvider;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.domain.user.persistence.entity.UserRole;

public class UserFixtures {

	/**
	 * NAME
	 */
	public static final String GENERAL_USER1_USERNAME = "GENERAL_USERNAME";
	public static final String OAUTH_USER1_USERNAME = "OAUTH_USERNAME";

	/**
	 * PHONE_NUMBER
	 */
	public static final String GENERAL_USER1_PHONENUMBER = "010-1234-5678";

	/**
	 * USER_ID
	 */
	public static final String GENERAL_USER1_USER_ID = "GENERAL_USER_ID";

	/**
	 * PASSWORD
	 */
	public static final String GENERAL_USER1_PASSWORD = "GENERAL_PASSWORD";

	/**
	 * PROVIDER
	 */
	public static final OAuthProvider OAUTHE_USER1_PROVIDER = OAuthProvider.APPLE;

	/**
	 * EMAIL
	 */
	public static final String OAUTH_USER1_EMAIL = "OAUTH_EMAIL";

	/**
	 * ENTITY
	 */
	public static User GENERAL_USER() {
		return UserMapper.toGeneralUser(GENERAL_USER1_USERNAME + UUID.randomUUID(), GENERAL_USER1_USER_ID,
			GENERAL_USER1_PASSWORD, GENERAL_USER1_PHONENUMBER);
	}

	public static User OAUTH_USER() {
		return UserMapper.toOAuthUser(OAUTH_USER1_USERNAME + UUID.randomUUID(), OAUTHE_USER1_PROVIDER,
			OAUTH_USER1_EMAIL);
	}

	/**
	 * ERROR ENTITY
	 */
	public static User ERROR_GENERAL_USER_IN_OAUTH_FIELDS() {
		return User.builder()
			.role(UserRole.USER)
			.nickname(GENERAL_USER1_USERNAME)
			.loginId(GENERAL_USER1_USER_ID)
			.password(GENERAL_USER1_PASSWORD)
			.provider(OAUTHE_USER1_PROVIDER)
			.build();
	}

	public static User ERROR_OAUTH_USER_IN_GENERAL_FIELDS() {
		return User.builder()
			.role(UserRole.USER)
			.nickname(OAUTH_USER1_USERNAME)
			.phoneNumber(GENERAL_USER1_PHONENUMBER)
			.provider(OAUTHE_USER1_PROVIDER)
			.email(OAUTH_USER1_EMAIL)
			.build();
	}

	public static User ERROR_USER_NOT_IDENTIFIER() {
		return User.builder()
			.role(UserRole.USER)
			.nickname(GENERAL_USER1_USERNAME)
			.build();
	}
}
