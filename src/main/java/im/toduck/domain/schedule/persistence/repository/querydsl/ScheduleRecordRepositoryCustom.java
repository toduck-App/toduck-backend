package im.toduck.domain.schedule.persistence.repository.querydsl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import im.toduck.domain.schedule.persistence.entity.ScheduleRecord;

public interface ScheduleRecordRepositoryCustom {
	List<ScheduleRecord> findByScheduleAndBetweenStartDateAndEndDate(Long scheduleId, LocalDate startDate,
		LocalDate endDate);

	Optional<ScheduleRecord> findScheduleRecordFetchJoinSchedule(Long scheduleRecordId);
}
