package im.toduck.domain.user.persistence.entity;

import static org.assertj.core.api.SoftAssertions.*;

import org.junit.jupiter.api.Test;

import im.toduck.fixtures.user.UserFixtures;
import im.toduck.global.exception.CommonException;

class UserTest {

	@Test
	void 일반유저_OAuth유저_중_하나의_식별자가_있으면_다른_식별자는_null이어야한다() {
		// given -> when -> then
		assertSoftly(softly -> {
			softly.assertThatThrownBy(
					UserFixtures::ERROR_GENERAL_USER_IN_OAUTH_FIELDS)
				.isInstanceOf(CommonException.class);
			softly.assertThatThrownBy(
					UserFixtures::ERROR_OAUTH_USER_IN_GENERAL_FIELDS)
				.isInstanceOf(CommonException.class);
			softly.assertThatThrownBy(
					UserFixtures::ERROR_USER_NOT_IDENTIFIER)
				.isInstanceOf(CommonException.class);

			softly.assertThatCode(
					UserFixtures::GENERAL_USER)
				.doesNotThrowAnyException();
			softly.assertThatCode(
					UserFixtures::OAUTH_USER)
				.doesNotThrowAnyException();
		});
	}

}
