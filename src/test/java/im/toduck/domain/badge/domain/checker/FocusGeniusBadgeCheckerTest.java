package im.toduck.domain.badge.domain.checker;

import static im.toduck.fixtures.user.UserFixtures.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.ServiceTest;
import im.toduck.domain.badge.persistence.entity.BadgeCode;
import im.toduck.domain.concentration.persistence.entity.Concentration;
import im.toduck.domain.user.persistence.entity.User;

@Transactional
class FocusGeniusBadgeCheckerTest extends ServiceTest {

	@Autowired
	private FocusGeniusBadgeChecker focusGeniusBadgeChecker;

	private User user;

	@BeforeEach
	void setUp() {
		user = testFixtureBuilder.buildUser(GENERAL_USER());
	}

	@Test
	@DisplayName("뱃지 코드는 FOCUS_GENIUS여야 한다")
	void getBadgeCode() {
		assertThat(focusGeniusBadgeChecker.getBadgeCode()).isEqualTo(BadgeCode.FOCUS_GENIUS);
	}

	@Test
	@DisplayName("타이머 달성 횟수 합계가 15회 이상이면 true를 반환한다")
	void checkCondition_True() {
		// given
		// targetCount 15인 Concentration 생성
		createConcentrationWithTargetCount(15);

		// when
		boolean result = focusGeniusBadgeChecker.checkCondition(user);

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("타이머 달성 횟수 합계가 15회 미만이면 false를 반환한다")
	void checkCondition_False() {
		// given
		// targetCount 14인 Concentration 생성
		createConcentrationWithTargetCount(14);

		// when
		boolean result = focusGeniusBadgeChecker.checkCondition(user);

		// then
		assertThat(result).isFalse();
	}

	private void createConcentrationWithTargetCount(int targetCount) {
		Concentration concentration = Concentration.builder()
			.user(user)
			.date(LocalDate.now())
			.build();

		concentration.addTargetCount(targetCount);
		testFixtureBuilder.buildConcentration(concentration);
	}
}
