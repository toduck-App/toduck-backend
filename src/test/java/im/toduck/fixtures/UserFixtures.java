package im.toduck.fixtures;

import im.toduck.domain.user.common.mapper.UserMapper;
import im.toduck.domain.user.persistence.entity.User;

public class UserFixtures {

	/**
	 * NAME
	 */
	public static final String GENERAL_USER1_USERNAME = "GENERAL_USERNAME";

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
	 * ENTITY
	 */
	public static User GENERAL_USER() {
		return UserMapper.createGeneralUser(GENERAL_USER1_USERNAME, GENERAL_USER1_USER_ID, GENERAL_USER1_PASSWORD,
			GENERAL_USER1_PHONENUMBER);
	}
}
