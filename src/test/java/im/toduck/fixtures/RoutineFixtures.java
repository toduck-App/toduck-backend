package im.toduck.fixtures;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.test.util.ReflectionTestUtils;

import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.vo.PlanCategoryColor;
import im.toduck.domain.routine.persistence.vo.RoutineMemo;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.helper.DaysOfWeekBitmask;

public class RoutineFixtures {

	private static final LocalTime MORNING_TIME = LocalTime.of(7, 0);
	private static final LocalTime NOON_TIME = LocalTime.of(12, 0);
	private static final LocalTime EVENING_TIME = LocalTime.of(19, 0);
	private static final LocalTime NIGHT_TIME = LocalTime.of(22, 0);

	public static Routine MONDAY_ONLY_MORNING_ROUTINE(User user) {
		return Routine.builder()
			.user(user)
			.title("월요일 아침 루틴")
			.daysOfWeekBitmask(DaysOfWeekBitmask.createByDayOfWeek(List.of(DayOfWeek.MONDAY)))
			.time(MORNING_TIME)
			.color(PlanCategoryColor.from("#FF0000"))
			.isPublic(true)
			.build();
	}

	public static Routine MONDAY_ONLY_MORNING_ROUTINE_ALL_DAY(User user) {
		return Routine.builder()
			.user(user)
			.title("월요일 아침 루틴")
			.daysOfWeekBitmask(DaysOfWeekBitmask.createByDayOfWeek(List.of(DayOfWeek.MONDAY)))
			.time(null)
			.color(PlanCategoryColor.from("#FF0000"))
			.isPublic(true)
			.build();
	}

	public static Routine WEEKDAY_MORNING_ROUTINE(User user) {
		return Routine.builder()
			.user(user)
			.title("평일 아침 루틴")
			.daysOfWeekBitmask(DaysOfWeekBitmask.createByDayOfWeek(
				List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY,
					DayOfWeek.FRIDAY)))
			.time(MORNING_TIME)
			.color(PlanCategoryColor.from("#0000FF"))
			.isPublic(false)
			.build();
	}

	public static Routine WEEKEND_NOON_ROUTINE(User user) {
		return Routine.builder()
			.user(user)
			.title("주말 점심 루틴")
			.daysOfWeekBitmask(DaysOfWeekBitmask.createByDayOfWeek(List.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)))
			.time(NOON_TIME)
			.color(PlanCategoryColor.from("#00FF00"))
			.memo(RoutineMemo.from("주말 점심 루틴 메모"))
			.isPublic(true)
			.build();
	}

	public static Routine DAILY_EVENING_ROUTINE(User user) {
		return Routine.builder()
			.user(user)
			.title("매일 저녁 루틴")
			.daysOfWeekBitmask(DaysOfWeekBitmask.createByDayOfWeek(Arrays.asList(DayOfWeek.values())))
			.time(EVENING_TIME)
			.color(PlanCategoryColor.from("#FFA500"))
			.isPublic(true)
			.build();
	}

	public static Routine TUESDAY_THURSDAY_SATURDAY_NIGHT_ROUTINE(User user) {
		return Routine.builder()
			.user(user)
			.title("화목토 밤 루틴")
			.daysOfWeekBitmask(
				DaysOfWeekBitmask.createByDayOfWeek(List.of(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY, DayOfWeek.SATURDAY)))
			.time(NIGHT_TIME)
			.color(PlanCategoryColor.from("#800080"))
			.isPublic(false)
			.build();
	}

	public static Routine WEDNESDAY_FRIDAY_MORNING_ROUTINE(User user) {
		return Routine.builder()
			.user(user)
			.title("수금 아침 루틴")
			.daysOfWeekBitmask(DaysOfWeekBitmask.createByDayOfWeek(List.of(DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)))
			.time(MORNING_TIME)
			.color(PlanCategoryColor.from("#FFA500"))
			.isPublic(true)
			.build();
	}

	public static Routine EVERYDAY_EXCEPT_SUNDAY_NOON_ROUTINE(User user) {
		return Routine.builder()
			.user(user)
			.title("일요일 제외 매일 점심 루틴")
			.daysOfWeekBitmask(DaysOfWeekBitmask.createByDayOfWeek(
				List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY,
					DayOfWeek.SATURDAY)))
			.time(NOON_TIME)
			.color(PlanCategoryColor.from("#008080"))
			.isPublic(false)
			.build();
	}

	public static Routine FIRST_DAY_OF_WEEK_MORNING_ROUTINE(User user) {
		return Routine.builder()
			.user(user)
			.title("월요일 아침 주간 시작 루틴")
			.daysOfWeekBitmask(DaysOfWeekBitmask.createByDayOfWeek(List.of(DayOfWeek.MONDAY)))
			.time(MORNING_TIME)
			.color(PlanCategoryColor.from("#4B0082"))
			.isPublic(true)
			.build();
	}

	public static Routine LAST_DAY_OF_WEEK_NIGHT_ROUTINE(User user) {
		return Routine.builder()
			.user(user)
			.title("일요일 밤 주간 마무리 루틴")
			.daysOfWeekBitmask(DaysOfWeekBitmask.createByDayOfWeek(List.of(DayOfWeek.SUNDAY)))
			.time(NIGHT_TIME)
			.color(PlanCategoryColor.from("#006400"))
			.isPublic(false)
			.build();

	}

	public static Routine DELETED_MONDAY_ONLY_MORNING_ROUTINE(User user, LocalDateTime dateTime) {
		Routine routine = MONDAY_ONLY_MORNING_ROUTINE(user);
		ReflectionTestUtils.setField(routine, "deletedAt", dateTime);

		return routine;
	}

	public static Routine PRIVATE_ROUTINE(User user) {
		return Routine.builder()
			.user(user)
			.title("비공개 루틴")
			.daysOfWeekBitmask(DaysOfWeekBitmask.createByDayOfWeek(List.of(DayOfWeek.SUNDAY)))
			.time(NIGHT_TIME)
			.color(PlanCategoryColor.from("#006400"))
			.isPublic(false)
			.build();

	}

}
