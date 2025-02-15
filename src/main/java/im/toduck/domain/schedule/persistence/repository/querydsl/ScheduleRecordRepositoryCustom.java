package im.toduck.domain.schedule.persistence.repository.querydsl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import im.toduck.domain.schedule.persistence.entity.ScheduleRecord;

public interface ScheduleRecordRepositoryCustom {
	Optional<ScheduleRecord> findScheduleRecordByUserIdAndRecordDateAndScheduleId(
		LocalDate localDate,
		Long aLong);

	List<ScheduleRecord> findByScheduleAndBetweenStartDateAndEndDate(Long scheduleId, LocalDate startDate,
		LocalDate endDate);

	Optional<ScheduleRecord> findScheduleRecordFetchJoinSchedule(Long scheduleRecordId);

	void deleteByScheduleIdAndRecordDate(Long id, LocalDate startDate);

	List<ScheduleRecord> findByCompletedScheduleAndAfterStartDate(Long scheduleId,
		LocalDate startDate);

	void deleteByNonCompletedScheduleAndBetweenStartDateAndEndDate(Long scheduleId,
		LocalDate startDate,
		LocalDate endDate);

	void softDeleteByScheduleIdAndRecordDate(Long id, LocalDate localDate);
}
