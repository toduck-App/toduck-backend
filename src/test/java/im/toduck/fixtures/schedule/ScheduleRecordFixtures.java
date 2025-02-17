package im.toduck.fixtures.schedule;

import java.time.LocalDate;

import im.toduck.domain.schedule.persistence.entity.Schedule;
import im.toduck.domain.schedule.persistence.entity.ScheduleRecord;

public class ScheduleRecordFixtures {

	// 완료 여부
	private static final Boolean IS_COMPLETE = true;
	private static final Boolean IS_NOT_COMPLETE = false;

	public static ScheduleRecord IS_COMPLETE_SCHEDULE_RECORD(LocalDate recordDate, Schedule schedule) {
		return ScheduleRecord.builder()
			.isCompleted(IS_COMPLETE)
			.schedule(schedule)
			.recordDate(recordDate)
			.build();
	}

	public static ScheduleRecord IS_NOT_COMPLETE_SCHEDULE_RECORD(LocalDate recordDate, Schedule schedule) {
		return ScheduleRecord.builder()
			.isCompleted(IS_NOT_COMPLETE)
			.schedule(schedule)
			.recordDate(recordDate)
			.build();
	}
}
