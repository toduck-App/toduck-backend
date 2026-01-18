package im.toduck.domain.badge.domain.checker;

import static im.toduck.fixtures.social.SocialFixtures.*;
import static im.toduck.fixtures.user.UserFixtures.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.ServiceTest;
import im.toduck.domain.badge.persistence.entity.BadgeCode;
import im.toduck.domain.user.persistence.entity.User;

@Transactional
class QuackQuackBadgeCheckerTest extends ServiceTest {

	@Autowired
	private QuackQuackBadgeChecker quackQuackBadgeChecker;

	private User user;

	@BeforeEach
	void setUp() {
		user = testFixtureBuilder.buildUser(GENERAL_USER());
	}

	@Test
	@DisplayName("뱃지 코드는 QUACK_QUACK이어야 한다")
	void getBadgeCode() {
		assertThat(quackQuackBadgeChecker.getBadgeCode()).isEqualTo(BadgeCode.QUACK_QUACK);
	}

	@Test
	@DisplayName("소셜 게시글이 15개 이상이면 true를 반환한다")
	void checkCondition_True() {
		// given
		for (int i = 0; i < 15; i++) {
			testFixtureBuilder.buildSocial(SINGLE_SOCIAL(user, false));
		}

		// when
		boolean result = quackQuackBadgeChecker.checkCondition(user);

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("소셜 게시글이 15개 미만이면 false를 반환한다")
	void checkCondition_False() {
		// given
		for (int i = 0; i < 14; i++) {
			testFixtureBuilder.buildSocial(SINGLE_SOCIAL(user, false));
		}

		// when
		boolean result = quackQuackBadgeChecker.checkCondition(user);

		// then
		assertThat(result).isFalse();
	}
}
