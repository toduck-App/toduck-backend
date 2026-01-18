package im.toduck.domain.badge.domain.checker;

import static im.toduck.fixtures.routine.RoutineFixtures.*;
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
class PerfectionistBadgeCheckerTest extends ServiceTest {

	@Autowired
	private PerfectionistBadgeChecker perfectionistBadgeChecker;

	private User user;

	@BeforeEach
	void setUp() {
		user = testFixtureBuilder.buildUser(GENERAL_USER());
	}

	@Test
	@DisplayName("뱃지 코드는 PERFECTIONIST여야 한다")
	void getBadgeCode() {
		assertThat(perfectionistBadgeChecker.getBadgeCode()).isEqualTo(BadgeCode.PERFECTIONIST);
	}

	@Test
	@DisplayName("루틴이 10개 이상이면 true를 반환한다")
	void checkCondition_True() {
		// given
		for (int i = 0; i < 10; i++) {
			testFixtureBuilder.buildRoutineAndUpdateAuditFields(PUBLIC_MONDAY_MORNING_ROUTINE(user).build());
		}

		// when
		boolean result = perfectionistBadgeChecker.checkCondition(user);

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("루틴이 10개 미만이면 false를 반환한다")
	void checkCondition_False() {
		// given
		for (int i = 0; i < 9; i++) {
			testFixtureBuilder.buildRoutineAndUpdateAuditFields(PUBLIC_MONDAY_MORNING_ROUTINE(user).build());
		}

		// when
		boolean result = perfectionistBadgeChecker.checkCondition(user);

		// then
		assertThat(result).isFalse();
	}
}
