package im.toduck.domain.schedule.common.mapper;

import java.time.LocalDate;

import im.toduck.domain.schedule.persistence.entity.Schedule;
import im.toduck.domain.schedule.persistence.entity.ScheduleRecord;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleCompleteRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ScheduleRecordMapper {

	public static ScheduleRecord toScheduleRecord(Schedule schedule, ScheduleCompleteRequest scheduleCompleteRequest) {
		return ScheduleRecord
			.create(schedule, scheduleCompleteRequest.queryDate());
	}

	public static ScheduleRecord toSoftDeletedScheduleRecord(Schedule schedule, LocalDate queryDate) {
		ScheduleRecord scheduleRecord = ScheduleRecord
			.create(schedule, queryDate);
		scheduleRecord.softDelete();
		return scheduleRecord;
	}
}
