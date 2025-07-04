package im.toduck.fixtures.schedule;

import java.time.LocalDate;

import im.toduck.domain.schedule.persistence.entity.Schedule;
import im.toduck.domain.schedule.persistence.entity.ScheduleRecord;

public class ScheduleRecordFixtures {

	// 완료 여부
	private static final Boolean IS_COMPLETE = true;
	private static final Boolean IS_NOT_COMPLETE = false;

	public static ScheduleRecord IS_COMPLETE_SCHEDULE_RECORD(LocalDate recordDate, Schedule schedule) {
		ScheduleRecord scheduleRecord = ScheduleRecord.create(schedule, recordDate);
		scheduleRecord.changeComplete(IS_COMPLETE);
		return scheduleRecord;
	}

	public static ScheduleRecord IS_NOT_COMPLETE_SCHEDULE_RECORD(LocalDate recordDate, Schedule schedule) {
		return ScheduleRecord.create(schedule, recordDate);
	}
}
