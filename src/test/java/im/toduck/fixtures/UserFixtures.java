package im.toduck.fixtures;

import im.toduck.domain.user.persistence.entity.User;

public class UserFixtures {

	/**
	 * NAME
	 */
	public static final String USER1_USERNAME = "GENERAL_USERNAME";

	/**
	 * PHONE_NUMBER
	 */
	public static final String USER1_EMAIL = "010-1234-5678";

	/**
	 * PASSWORD
	 */
	public static final String USER1_PASSWORD = "GENERAL_PASSWORD";

	/**
	 * ENTITY
	 */
	public static User GENERAL_USER() {
		return User.createGeneralUser(USER1_USERNAME, USER1_PASSWORD, USER1_EMAIL);
	}

}
