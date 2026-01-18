package im.toduck.domain.badge.domain.checker;

import static im.toduck.fixtures.diary.DiaryFixtures.*;
import static im.toduck.fixtures.user.UserFixtures.*;
import static org.assertj.core.api.Assertions.*;

import java.time.YearMonth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.ServiceTest;
import im.toduck.domain.badge.persistence.entity.BadgeCode;
import im.toduck.domain.user.persistence.entity.User;

@Transactional
class DailyDiaryBadgeCheckerTest extends ServiceTest {

	@Autowired
	private DailyDiaryBadgeChecker dailyDiaryBadgeChecker;

	private User user;

	@BeforeEach
	void setUp() {
		user = testFixtureBuilder.buildUser(GENERAL_USER());
	}

	@Test
	@DisplayName("뱃지 코드는 DAILY_DIARY여야 한다")
	void getBadgeCode() {
		assertThat(dailyDiaryBadgeChecker.getBadgeCode()).isEqualTo(BadgeCode.DAILY_DIARY);
	}

	@Test
	@DisplayName("이번 달 일기 작성률이 50% 이상이면 true를 반환한다")
	void checkCondition_True() {
		// given
		YearMonth currentMonth = YearMonth.now();
		int daysInMonth = currentMonth.lengthOfMonth();
		int requiredDays = (int)Math.ceil(daysInMonth * 0.5);

		for (int i = 1; i <= requiredDays; i++) {
			testFixtureBuilder.buildDiary(DIARY(user, currentMonth.atDay(i)));
		}

		// when
		boolean result = dailyDiaryBadgeChecker.checkCondition(user);

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("이번 달 일기 작성률이 50% 미만이면 false를 반환한다")
	void checkCondition_False() {
		// given
		YearMonth currentMonth = YearMonth.now();
		int daysInMonth = currentMonth.lengthOfMonth();
		int insufficientDays = (int)Math.ceil(daysInMonth * 0.5) - 1;

		for (int i = 1; i <= insufficientDays; i++) {
			testFixtureBuilder.buildDiary(DIARY(user, currentMonth.atDay(i)));
		}

		// when
		boolean result = dailyDiaryBadgeChecker.checkCondition(user);

		// then
		assertThat(result).isFalse();
	}
}
