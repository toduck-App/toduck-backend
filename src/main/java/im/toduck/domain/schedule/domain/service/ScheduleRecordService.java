package im.toduck.domain.schedule.domain.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import im.toduck.domain.schedule.common.mapper.ScheduleRecordMapper;
import im.toduck.domain.schedule.persistence.entity.Schedule;
import im.toduck.domain.schedule.persistence.entity.ScheduleRecord;
import im.toduck.domain.schedule.persistence.repository.ScheduleRecordRepository;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleCompleteRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleRecordService {
	private final ScheduleRecordRepository scheduleRecordRepository;

	public Optional<ScheduleRecord> getScheduleRecordWithSchedule(Long userId,
		ScheduleCompleteRequest scheduleCompleteRequest) {
		return scheduleRecordRepository
			.findScheduleRecordByRecordDateAndScheduleId(
				scheduleCompleteRequest.queryDate(),
				scheduleCompleteRequest.scheduleId());
	}

	public void completeScheduleRecord(ScheduleRecord scheduleRecord, ScheduleCompleteRequest scheduleCompleteRequest) {
		scheduleRecord.changeComplete(scheduleCompleteRequest.isComplete());
	}

	public void createScheduleRecord(Schedule schedule, ScheduleCompleteRequest scheduleCompleteRequest) {
		ScheduleRecord scheduleRecord = ScheduleRecordMapper.toScheduleRecord(schedule, scheduleCompleteRequest);
		scheduleRecordRepository.save(scheduleRecord);
	}
}
