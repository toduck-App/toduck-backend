package im.toduck.domain.badge.domain.checker;

import static im.toduck.fixtures.user.UserFixtures.*;
import static org.assertj.core.api.Assertions.*;

import java.time.DayOfWeek;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.ServiceTest;
import im.toduck.domain.badge.persistence.entity.BadgeCode;
import im.toduck.domain.person.persistence.entity.PlanCategory;
import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.vo.PlanCategoryColor;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.helper.DaysOfWeekBitmask;

@Transactional
class CrowBadgeCheckerTest extends ServiceTest {

	@Autowired
	private CrowBadgeChecker crowBadgeChecker;

	private User user;

	@BeforeEach
	void setUp() {
		user = testFixtureBuilder.buildUser(GENERAL_USER());
	}

	@Test
	@DisplayName("뱃지 코드는 CROW여야 한다")
	void getBadgeCode() {
		assertThat(crowBadgeChecker.getBadgeCode()).isEqualTo(BadgeCode.CROW);
	}

	@Test
	@DisplayName("루틴 카테고리가 5개 이상이면 true를 반환한다")
	void checkCondition_True() {
		// given
		// 5개의 서로 다른 카테고리 루틴 생성
		createRoutineWithCategory(PlanCategory.COMPUTER);
		createRoutineWithCategory(PlanCategory.FOOD);
		createRoutineWithCategory(PlanCategory.PENCIL);
		createRoutineWithCategory(PlanCategory.RED_BOOK);
		createRoutineWithCategory(PlanCategory.YELLOW_BOOK);

		// when
		boolean result = crowBadgeChecker.checkCondition(user);

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("루틴 카테고리가 5개 미만이면 false를 반환한다")
	void checkCondition_False() {
		// given
		// 4개의 서로 다른 카테고리 루틴 생성 (하나는 중복)
		createRoutineWithCategory(PlanCategory.COMPUTER);
		createRoutineWithCategory(PlanCategory.FOOD);
		createRoutineWithCategory(PlanCategory.PENCIL);
		createRoutineWithCategory(PlanCategory.RED_BOOK);
		createRoutineWithCategory(PlanCategory.RED_BOOK); // 중복 카테고리

		// when
		boolean result = crowBadgeChecker.checkCondition(user);

		// then
		assertThat(result).isFalse();
	}

	private void createRoutineWithCategory(PlanCategory category) {
		Routine routine = Routine.builder()
			.user(user)
			.title("Test Routine")
			.category(category)
			.color(PlanCategoryColor.from("#FF0000"))
			.isPublic(true)
			.daysOfWeekBitmask(DaysOfWeekBitmask.createByDayOfWeek(List.of(DayOfWeek.MONDAY)))
			.build();
		testFixtureBuilder.buildRoutine(routine);
	}
}
