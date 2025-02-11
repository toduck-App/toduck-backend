package im.toduck.domain.schedule.persistence.repository.querydsl;

import java.time.LocalDate;
import java.util.List;

import im.toduck.domain.schedule.persistence.entity.ScheduleRecord;

public interface ScheduleRecordRepositoryCustom {
	List<ScheduleRecord> findByScheduleAndBetweenStartDateAndEndDate(Long scheduleId, LocalDate startDate,
		LocalDate endDate);
}
