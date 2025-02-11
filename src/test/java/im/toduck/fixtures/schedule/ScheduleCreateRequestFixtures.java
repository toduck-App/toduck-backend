package im.toduck.fixtures.schedule;

import static im.toduck.fixtures.schedule.ScheduleFixtures.*;

import im.toduck.domain.schedule.presentation.dto.request.ScheduleCreateRequest;

public class ScheduleCreateRequestFixtures {

	public static ScheduleCreateRequest NON_REPEATABLE_ONE_DAY_SCHEDULE_CREATE_REQUEST() { // 반복 없는 하루 일정 생성 요청
		return ScheduleCreateRequest.builder()
			.title(DEFAULT_TITLE)
			.category(DEFAULT_CATEGORY)
			.color(DEFAULT_COLOR)
			.startDate(TWO_DAY_DATE)
			.endDate(TWO_DAY_DATE)
			.isAllDay(TRUE_IS_ALL_DAY)
			.time(NULL_TIME)
			.alarm(NULL_ALARM)
			.daysOfWeek(NULL_DAYS_OF_WEEK)
			.location(DEFAULT_LOCATION)
			.memo(DEFAULT_MEMO)
			.build();
	}

	public static ScheduleCreateRequest REPEATABLE_ONE_DAY_SCHEDULE_CREATE_REQUEST() { // 반복 있는 하루 일정 생성 요청
		return ScheduleCreateRequest.builder()
			.title(DEFAULT_TITLE)
			.category(DEFAULT_CATEGORY)
			.color(DEFAULT_COLOR)
			.startDate(TWO_DAY_DATE)
			.endDate(TWO_DAY_DATE)
			.isAllDay(TRUE_IS_ALL_DAY)
			.time(NULL_TIME)
			.alarm(NULL_ALARM)
			.daysOfWeek(NON_NULL_DAYS_OF_WEEK)
			.location(DEFAULT_LOCATION)
			.memo(DEFAULT_MEMO)
			.build();
	}

	public static ScheduleCreateRequest NON_REPEATABLE_DAYS_SCHEDULE_CREATE_REQUEST() { // 반복 없는 기간 일정 생성 요청
		return ScheduleCreateRequest.builder()
			.title(DEFAULT_TITLE)
			.category(DEFAULT_CATEGORY)
			.color(DEFAULT_COLOR)
			.startDate(TWO_DAY_DATE)
			.endDate(TWENTY_FOUR_DATE)
			.isAllDay(FALSE_IS_ALL_DAY)
			.time(NON_NULL_TIME)
			.alarm(NON_NULL_ALARM)
			.daysOfWeek(NULL_DAYS_OF_WEEK)
			.location(DEFAULT_LOCATION)
			.memo(DEFAULT_MEMO)
			.build();
	}

	public static ScheduleCreateRequest REPEATABLE_DAYS_SCHEDULE_CREATE_REQUEST() { // 반복 있는 기간 일정 생성 요청
		return ScheduleCreateRequest.builder()
			.title(DEFAULT_TITLE)
			.category(DEFAULT_CATEGORY)
			.color(DEFAULT_COLOR)
			.startDate(TWO_DAY_DATE)
			.endDate(TWENTY_FOUR_DATE)
			.isAllDay(FALSE_IS_ALL_DAY)
			.time(NON_NULL_TIME)
			.alarm(NON_NULL_ALARM)
			.daysOfWeek(NON_NULL_DAYS_OF_WEEK)
			.location(DEFAULT_LOCATION)
			.memo(DEFAULT_MEMO)
			.build();
	}

	public static ScheduleCreateRequest DAYS_OF_WEEK_NULL_REQUEST() { // 반복 요일 : null
		return ScheduleCreateRequest.builder()
			.title(DEFAULT_TITLE)
			.category(DEFAULT_CATEGORY)
			.color(DEFAULT_COLOR)
			.startDate(TWO_DAY_DATE)
			.endDate(TWO_DAY_DATE)
			.isAllDay(TRUE_IS_ALL_DAY)
			.time(NULL_TIME)
			.alarm(NULL_ALARM)
			.daysOfWeek(NULL_DAYS_OF_WEEK) // 반복 요일 : null
			.location(DEFAULT_LOCATION)
			.memo(DEFAULT_MEMO)
			.build();
	}

	public static ScheduleCreateRequest ERROR_TRUE_IS_ALL_DAY_TIME_NON_NULL_REQUEST() { // 종일 : true, 시간 : null 아님
		return ScheduleCreateRequest.builder()
			.title(DEFAULT_TITLE)
			.category(DEFAULT_CATEGORY)
			.color(DEFAULT_COLOR)
			.startDate(TWO_DAY_DATE)
			.endDate(TWO_DAY_DATE)
			.isAllDay(TRUE_IS_ALL_DAY) // 종일 : true
			.time(NON_NULL_TIME) // 시간 : null 아님
			.alarm(NULL_ALARM)
			.daysOfWeek(NULL_DAYS_OF_WEEK)
			.location(DEFAULT_LOCATION)
			.memo(DEFAULT_MEMO)
			.build();
	}

	public static ScheduleCreateRequest ERROR_FALSE_IS_ALL_DAY_TIME_NULL_REQUEST() { // 종일 : false, 시간 : null
		return ScheduleCreateRequest.builder()
			.title(DEFAULT_TITLE)
			.category(DEFAULT_CATEGORY)
			.color(DEFAULT_COLOR)
			.startDate(TWO_DAY_DATE)
			.endDate(TWO_DAY_DATE)
			.isAllDay(FALSE_IS_ALL_DAY) // 종일 : false
			.time(NULL_TIME) // 시간 : null
			.alarm(NON_NULL_ALARM)
			.daysOfWeek(NULL_DAYS_OF_WEEK)
			.location(DEFAULT_LOCATION)
			.memo(DEFAULT_MEMO)
			.build();
	}

	public static ScheduleCreateRequest ERROR_TRUE_IS_ALL_DAY_ALARM_NON_NULL_REQUEST() { // 종일 : true, 알람 : null 아님
		return ScheduleCreateRequest.builder()
			.title(DEFAULT_TITLE)
			.category(DEFAULT_CATEGORY)
			.color(DEFAULT_COLOR)
			.startDate(TWO_DAY_DATE)
			.endDate(TWO_DAY_DATE)
			.isAllDay(TRUE_IS_ALL_DAY) // 종일 : true
			.time(NULL_TIME)
			.alarm(NON_NULL_ALARM) // 알람 : null 아님
			.daysOfWeek(NULL_DAYS_OF_WEEK)
			.location(DEFAULT_LOCATION)
			.memo(DEFAULT_MEMO)
			.build();
	}

	public static ScheduleCreateRequest ERROR_START_DATE_GREATER_THAN_END_DATE_REQUEST() { // 시작 날짜가 종료 날짜보다 큼
		return ScheduleCreateRequest.builder()
			.title(DEFAULT_TITLE)
			.category(DEFAULT_CATEGORY)
			.color(DEFAULT_COLOR)
			.startDate(TWENTY_FOUR_DATE) // 시작 날짜가 종료 날짜보다 큼
			.endDate(TWO_DAY_DATE)
			.isAllDay(FALSE_IS_ALL_DAY)
			.time(NON_NULL_TIME)
			.alarm(NON_NULL_ALARM)
			.daysOfWeek(NON_NULL_DAYS_OF_WEEK)
			.location(DEFAULT_LOCATION)
			.memo(DEFAULT_MEMO)
			.build();
	}
}
