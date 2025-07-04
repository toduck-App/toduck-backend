package im.toduck.fixtures.schedule;

import java.time.LocalDate;

import im.toduck.domain.schedule.presentation.dto.request.ScheduleCompleteRequest;

public class ScheduleCompleteFixtures {
	public static ScheduleCompleteRequest CUSTOM_REQUEST(
		Long scheduleId,
		boolean isComplete,
		LocalDate queryDate
	) {
		return ScheduleCompleteRequest.builder()
			.scheduleId(scheduleId)
			.isComplete(isComplete)
			.queryDate(queryDate)
			.build();
	}

	public static ScheduleCompleteRequest COMPLETE_REQUEST(Long scheduleId, LocalDate queryDate) {
		return CUSTOM_REQUEST(scheduleId, true, queryDate);
	}

	public static ScheduleCompleteRequest NON_COMPLETE_REQUEST(Long scheduleId, LocalDate queryDate) {
		return CUSTOM_REQUEST(scheduleId, false, queryDate);
	}

	public static ScheduleCompleteRequest COMPLETE_REQUEST(Long scheduleId) {
		return COMPLETE_REQUEST(scheduleId, LocalDate.now());
	}

	public static ScheduleCompleteRequest NON_COMPLETE_REQUEST(Long scheduleId) {
		return NON_COMPLETE_REQUEST(scheduleId, LocalDate.now());
	}

}
