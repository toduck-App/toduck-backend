package im.toduck.fixtures.routine;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.vo.PlanCategoryColor;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.helper.DaysOfWeekBitmask;

public class RoutineFixtures {

	private static final LocalTime MORNING_TIME = LocalTime.of(7, 0);
	private static final LocalTime NOON_TIME = LocalTime.of(12, 0);
	private static final LocalTime EVENING_TIME = LocalTime.of(19, 0);
	private static final LocalTime NIGHT_TIME = LocalTime.of(22, 0);

	public static RoutineWithAuditInfo.RoutineWithAuditInfoBuilder PUBLIC_MONDAY_MORNING_ROUTINE(User user) {
		Routine routine = Routine.builder()
			.user(user)
			.title("내용")
			.daysOfWeekBitmask(DaysOfWeekBitmask.createByDayOfWeek(List.of(DayOfWeek.MONDAY)))
			.time(MORNING_TIME)
			.color(PlanCategoryColor.from("#FF0000"))
			.isPublic(true)
			.build();

		return RoutineWithAuditInfo.builder()
			.routine(routine);
	}

	public static RoutineWithAuditInfo.RoutineWithAuditInfoBuilder PRIVATE_MONDAY_MORNING_ROUTINE(User user) {
		Routine routine = Routine.builder()
			.user(user)
			.title("내용")
			.daysOfWeekBitmask(DaysOfWeekBitmask.createByDayOfWeek(List.of(DayOfWeek.MONDAY)))
			.time(MORNING_TIME)
			.color(PlanCategoryColor.from("#FF0000"))
			.isPublic(false)
			.build();

		return RoutineWithAuditInfo.builder()
			.routine(routine);
	}

	public static RoutineWithAuditInfo.RoutineWithAuditInfoBuilder PUBLIC_MONDAY_ALLDAY_ROUTINE(User user) {
		Routine routine = Routine.builder()
			.user(user)
			.title("내용")
			.daysOfWeekBitmask(DaysOfWeekBitmask.createByDayOfWeek(List.of(DayOfWeek.MONDAY)))
			.time(null)
			.color(PlanCategoryColor.from("#FF0000"))
			.isPublic(true)
			.build();

		return RoutineWithAuditInfo.builder()
			.routine(routine);
	}

	public static RoutineWithAuditInfo.RoutineWithAuditInfoBuilder PUBLIC_DAILY_EVENING_ROUTINE(User user) {
		Routine routine = Routine.builder()
			.user(user)
			.title("매일 루틴")
			.daysOfWeekBitmask(DaysOfWeekBitmask.createByDayOfWeek(Arrays.asList(DayOfWeek.values())))
			.time(EVENING_TIME)
			.color(PlanCategoryColor.from("#FFA500"))
			.isPublic(true)
			.build();

		return RoutineWithAuditInfo.builder()
			.routine(routine);
	}

	public static RoutineWithAuditInfo.RoutineWithAuditInfoBuilder PUBLIC_WEEKDAY_MORNING_ROUTINE(User user) {
		Routine routine = Routine.builder()
			.user(user)
			.title("평일 루틴")
			.daysOfWeekBitmask(DaysOfWeekBitmask.createByDayOfWeek(
				List.of(DayOfWeek.MONDAY, java.time.DayOfWeek.TUESDAY, java.time.DayOfWeek.WEDNESDAY,
					java.time.DayOfWeek.THURSDAY,
					DayOfWeek.FRIDAY)))
			.time(MORNING_TIME)
			.color(PlanCategoryColor.from("#0000FF"))
			.isPublic(true) // 수정: false -> true
			.build();

		return RoutineWithAuditInfo.builder()
			.routine(routine);
	}

	public static RoutineWithAuditInfo.RoutineWithAuditInfoBuilder PUBLIC_TUESDAY_WEDNESDAY_THURSDAY_MORNING_ROUTINE(
		User user) {
		Routine routine = Routine.builder()
			.user(user)
			.title("평일 루틴")
			.daysOfWeekBitmask(DaysOfWeekBitmask.createByDayOfWeek(
				List.of(DayOfWeek.TUESDAY, java.time.DayOfWeek.WEDNESDAY, java.time.DayOfWeek.THURSDAY)))
			.time(MORNING_TIME)
			.color(PlanCategoryColor.from("#0000FF"))
			.isPublic(true) // 수정: false -> true
			.build();

		return RoutineWithAuditInfo.builder()
			.routine(routine);
	}

	public static RoutineWithAuditInfo.RoutineWithAuditInfoBuilder PRIVATE_DAILY_EVENING_ROUTINE(User user) {
		Routine routine = Routine.builder()
			.user(user)
			.title("매일 루틴")
			.daysOfWeekBitmask(DaysOfWeekBitmask.createByDayOfWeek(Arrays.asList(DayOfWeek.values())))
			.time(EVENING_TIME)
			.color(PlanCategoryColor.from("#FFA500"))
			.isPublic(false)
			.build();

		return RoutineWithAuditInfo.builder()
			.routine(routine);
	}

	public static RoutineWithAuditInfo.RoutineWithAuditInfoBuilder PRIVATE_WEEKDAY_MORNING_ROUTINE(User user) {
		Routine routine = Routine.builder()
			.user(user)
			.title("평일 루틴")
			.daysOfWeekBitmask(DaysOfWeekBitmask.createByDayOfWeek(
				List.of(DayOfWeek.MONDAY, java.time.DayOfWeek.TUESDAY, java.time.DayOfWeek.WEDNESDAY,
					java.time.DayOfWeek.THURSDAY,
					DayOfWeek.FRIDAY)))
			.time(MORNING_TIME)
			.color(PlanCategoryColor.from("#0000FF"))
			.isPublic(false)
			.build();

		return RoutineWithAuditInfo.builder()
			.routine(routine);
	}

	public static RoutineWithAuditInfo.RoutineWithAuditInfoBuilder PUBLIC_WEEKEND_NOON_ROUTINE(User user) {
		Routine routine = Routine.builder()
			.user(user)
			.title("주말 루틴")
			.daysOfWeekBitmask(
				DaysOfWeekBitmask.createByDayOfWeek(List.of(DayOfWeek.SATURDAY, java.time.DayOfWeek.SUNDAY)))
			.time(NOON_TIME)
			.color(PlanCategoryColor.from("#00FF00"))
			.isPublic(true)
			.build();

		return RoutineWithAuditInfo.builder()
			.routine(routine);
	}

	public static RoutineWithAuditInfo.RoutineWithAuditInfoBuilder PRIVATE_WEEKEND_NOON_ROUTINE(User user) {
		Routine routine = Routine.builder()
			.user(user)
			.title("주말 루틴")
			.daysOfWeekBitmask(
				DaysOfWeekBitmask.createByDayOfWeek(List.of(DayOfWeek.SATURDAY, java.time.DayOfWeek.SUNDAY)))
			.time(NOON_TIME)
			.color(PlanCategoryColor.from("#00FF00"))
			.isPublic(false)
			.build();

		return RoutineWithAuditInfo.builder()
			.routine(routine);
	}

	public static RoutineWithAuditInfo.RoutineWithAuditInfoBuilder PUBLIC_TUE_THU_SAT_NIGHT_ROUTINE(User user) {
		Routine routine = Routine.builder()
			.user(user)
			.title("화목토 루틴")
			.daysOfWeekBitmask(
				DaysOfWeekBitmask.createByDayOfWeek(
					List.of(DayOfWeek.TUESDAY, java.time.DayOfWeek.THURSDAY, java.time.DayOfWeek.SATURDAY)))
			.time(NIGHT_TIME)
			.color(PlanCategoryColor.from("#800080"))
			.isPublic(true)
			.build();

		return RoutineWithAuditInfo.builder()
			.routine(routine);
	}

	public static RoutineWithAuditInfo.RoutineWithAuditInfoBuilder PRIVATE_TUE_THU_SAT_NIGHT_ROUTINE(User user) {
		Routine routine = Routine.builder()
			.user(user)
			.title("화목토 루틴")
			.daysOfWeekBitmask(
				DaysOfWeekBitmask.createByDayOfWeek(
					List.of(DayOfWeek.TUESDAY, java.time.DayOfWeek.THURSDAY, java.time.DayOfWeek.SATURDAY)))
			.time(NIGHT_TIME)
			.color(PlanCategoryColor.from("#800080"))
			.isPublic(false)
			.build();

		return RoutineWithAuditInfo.builder()
			.routine(routine);
	}

	public static RoutineWithAuditInfo.RoutineWithAuditInfoBuilder PUBLIC_WED_FRI_MORNING_ROUTINE(User user) {
		Routine routine = Routine.builder()
			.user(user)
			.title("수금 루틴")
			.daysOfWeekBitmask(
				DaysOfWeekBitmask.createByDayOfWeek(List.of(DayOfWeek.WEDNESDAY, java.time.DayOfWeek.FRIDAY)))
			.time(MORNING_TIME)
			.color(PlanCategoryColor.from("#FFA500"))
			.isPublic(true)
			.build();

		return RoutineWithAuditInfo.builder()
			.routine(routine);
	}

	public static RoutineWithAuditInfo.RoutineWithAuditInfoBuilder PRIVATE_WED_FRI_MORNING_ROUTINE(User user) {
		Routine routine = Routine.builder()
			.user(user)
			.title("수금 루틴")
			.daysOfWeekBitmask(
				DaysOfWeekBitmask.createByDayOfWeek(List.of(DayOfWeek.WEDNESDAY, java.time.DayOfWeek.FRIDAY)))
			.time(MORNING_TIME)
			.color(PlanCategoryColor.from("#FFA500"))
			.isPublic(false)
			.build();

		return RoutineWithAuditInfo.builder()
			.routine(routine);
	}

	public static RoutineWithAuditInfo.RoutineWithAuditInfoBuilder PUBLIC_EXCEPT_SUNDAY_NOON_ROUTINE(User user) {
		Routine routine = Routine.builder()
			.user(user)
			.title("일요일 제외 매일 루틴")
			.daysOfWeekBitmask(DaysOfWeekBitmask.createByDayOfWeek(
				List.of(DayOfWeek.MONDAY, java.time.DayOfWeek.TUESDAY, java.time.DayOfWeek.WEDNESDAY,
					java.time.DayOfWeek.THURSDAY, java.time.DayOfWeek.FRIDAY,
					DayOfWeek.SATURDAY)))
			.time(NOON_TIME)
			.color(PlanCategoryColor.from("#008080"))
			.isPublic(true)
			.build();

		return RoutineWithAuditInfo.builder()
			.routine(routine);
	}

	public static RoutineWithAuditInfo.RoutineWithAuditInfoBuilder PRIVATE_EXCEPT_SUNDAY_NOON_ROUTINE(User user) {
		Routine routine = Routine.builder()
			.user(user)
			.title("일요일 제외 매일 루틴")
			.daysOfWeekBitmask(DaysOfWeekBitmask.createByDayOfWeek(
				List.of(DayOfWeek.MONDAY, java.time.DayOfWeek.TUESDAY, java.time.DayOfWeek.WEDNESDAY,
					java.time.DayOfWeek.THURSDAY, java.time.DayOfWeek.FRIDAY,
					DayOfWeek.SATURDAY)))
			.time(NOON_TIME)
			.color(PlanCategoryColor.from("#008080"))
			.isPublic(false)
			.build();

		return RoutineWithAuditInfo.builder()
			.routine(routine);
	}

	public static RoutineWithAuditInfo.RoutineWithAuditInfoBuilder PUBLIC_SUNDAY_NIGHT_ROUTINE(User user) {
		Routine routine = Routine.builder()
			.user(user)
			.title("일요일 루틴")
			.daysOfWeekBitmask(DaysOfWeekBitmask.createByDayOfWeek(List.of(DayOfWeek.SUNDAY)))
			.time(NIGHT_TIME)
			.color(PlanCategoryColor.from("#006400"))
			.isPublic(true)
			.build();

		return RoutineWithAuditInfo.builder()
			.routine(routine);
	}

	public static RoutineWithAuditInfo.RoutineWithAuditInfoBuilder PRIVATE_SUNDAY_NIGHT_ROUTINE(User user) {
		Routine routine = Routine.builder()
			.user(user)
			.title("일요일 루틴")
			.daysOfWeekBitmask(DaysOfWeekBitmask.createByDayOfWeek(List.of(DayOfWeek.SUNDAY)))
			.time(NIGHT_TIME)
			.color(PlanCategoryColor.from("#006400"))
			.isPublic(false)
			.build();

		return RoutineWithAuditInfo.builder()
			.routine(routine);
	}
}

