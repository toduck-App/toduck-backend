package im.toduck.domain.schedule.common.mapper;

import im.toduck.domain.schedule.persistence.entity.Schedule;
import im.toduck.domain.schedule.persistence.entity.ScheduleRecord;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleCompleteRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ScheduleRecordMapper {

	public static ScheduleRecord toScheduleRecord(Schedule schedule, ScheduleCompleteRequest scheduleCompleteRequest) {
		return ScheduleRecord.builder()
			.recordDate(scheduleCompleteRequest.queryDate())
			.isCompleted(scheduleCompleteRequest.isComplete())
			.schedule(schedule)
			.build();
	}
}
