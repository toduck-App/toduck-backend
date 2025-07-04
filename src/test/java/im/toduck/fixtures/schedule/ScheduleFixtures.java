package im.toduck.fixtures.schedule;

import static im.toduck.fixtures.schedule.ScheduleCreateRequestFixtures.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import im.toduck.domain.person.persistence.entity.PlanCategory;
import im.toduck.domain.routine.persistence.vo.PlanCategoryColor;
import im.toduck.domain.schedule.common.mapper.ScheduleMapper;
import im.toduck.domain.schedule.persistence.entity.Schedule;
import im.toduck.domain.schedule.persistence.vo.ScheduleAlram;
import im.toduck.domain.schedule.persistence.vo.ScheduleDate;
import im.toduck.domain.schedule.persistence.vo.ScheduleTime;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.helper.DaysOfWeekBitmask;

public class ScheduleFixtures {
	/* 일정 제목 */
	public static final String DEFAULT_TITLE = "Test Schedule";

	/* 일정 카테고리 */
	public static final PlanCategory DEFAULT_CATEGORY = PlanCategory.MEDICINE;

	/* 일정 카테고리 색 */
	public static final String DEFAULT_COLOR = "#000000";

	/* 일정 종일 여부 */
	public static final Boolean TRUE_IS_ALL_DAY = true;
	public static final Boolean FALSE_IS_ALL_DAY = false;

	/* 일정 시간 */
	public static final LocalTime NON_NULL_TIME = LocalTime.of(12, 0);
	public static final LocalTime NULL_TIME = null;

	/* 일정 알람 */
	public static final ScheduleAlram NON_NULL_ALARM = ScheduleAlram.TEN_MINUTE;
	public static final ScheduleAlram NULL_ALARM = null;

	/* 일정 날짜 */
	public static final LocalDate ONE_DAY_DATE = LocalDate.of(2025, 1, 1);
	public static final LocalDate TWO_DAY_DATE = LocalDate.of(2025, 1, 2);
	public static final LocalDate TWENTY_FOUR_DATE = LocalDate.of(2025, 1, 24);
	public static final LocalDate TWENTY_FIVE_DATE = LocalDate.of(2025, 1, 25);

	/* 일정 반복 요일 */
	public static final List<DayOfWeek> NON_NULL_DAYS_OF_WEEK = List.of(
		DayOfWeek.MONDAY,
		DayOfWeek.TUESDAY,
		DayOfWeek.WEDNESDAY,
		DayOfWeek.FRIDAY,
		DayOfWeek.SATURDAY);
	public static final List<DayOfWeek> NULL_DAYS_OF_WEEK = null;

	/* 일정 장소 */
	public static final String DEFAULT_LOCATION = "Test Location";

	/* 일정 메모 */
	public static final String DEFAULT_MEMO = "Test Memo";

	public static Schedule NON_REPEATABLE_ONE_DAY_SCHEDULE(User user) { // 반복 없는 하루 일정
		return ScheduleMapper.toSchedule(user, NON_REPEATABLE_ONE_DAY_SCHEDULE_CREATE_REQUEST());
	}

	public static Schedule REPEATABLE_ONE_DAY_SCHEDULE(User user) { // 반복 있는 하루 일정
		return ScheduleMapper.toSchedule(user, REPEATABLE_ONE_DAY_SCHEDULE_CREATE_REQUEST());
	}

	public static Schedule NON_REPEATABLE_DAYS_SCHEDULE(User user) { // 반복 없는 기간 일정
		return ScheduleMapper.toSchedule(user, NON_REPEATABLE_DAYS_SCHEDULE_CREATE_REQUEST());
	}

	public static Schedule REPEATABLE_DAYS_SCHEDULE(User user) { // 반복 있는 기간 일정
		return ScheduleMapper.toSchedule(user, REPEATABLE_DAYS_SCHEDULE_CREATE_REQUEST());
	}

	public static Schedule DEFAULT_NON_REPEATABLE_SCHEDULE(User user, LocalDate startDate, LocalDate endDate) { // 기본 일정
		return Schedule.builder()
			.user(user)
			.title(DEFAULT_TITLE)
			.category(DEFAULT_CATEGORY)
			.scheduleDate(ScheduleDate.of(startDate, endDate))
			.scheduleTime(ScheduleTime.of(TRUE_IS_ALL_DAY, NULL_TIME, NULL_ALARM))
			.color(PlanCategoryColor.from(DEFAULT_COLOR))
			.daysOfWeekBitmask(null)
			.location(DEFAULT_LOCATION)
			.memo(DEFAULT_MEMO)
			.build();
	}

	public static Schedule DEFAULT_REPEATABLE_SCHEDULE(User user, LocalDate startDate, LocalDate endDate) { // 기본 반복 일정
		return Schedule.builder()
			.user(user)
			.title(DEFAULT_TITLE)
			.category(DEFAULT_CATEGORY)
			.scheduleDate(ScheduleDate.of(startDate, endDate))
			.scheduleTime(ScheduleTime.of(TRUE_IS_ALL_DAY, NULL_TIME, NULL_ALARM))
			.color(PlanCategoryColor.from(DEFAULT_COLOR))
			.daysOfWeekBitmask(DaysOfWeekBitmask.createByDayOfWeek(NON_NULL_DAYS_OF_WEEK))
			.location(DEFAULT_LOCATION)
			.memo(DEFAULT_MEMO)
			.build();
	}

}
