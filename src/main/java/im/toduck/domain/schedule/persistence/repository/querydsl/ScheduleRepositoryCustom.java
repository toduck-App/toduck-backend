package im.toduck.domain.schedule.persistence.repository.querydsl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import im.toduck.domain.schedule.persistence.entity.Schedule;
import im.toduck.global.persistence.projection.DailyCount;

public interface ScheduleRepositoryCustom {
	List<Schedule> findSchedules(Long userId, LocalDate startDate, LocalDate endDate);

	List<DailyCount> countByCreatedAtBetweenGroupByDate(
		final LocalDateTime startDateTime,
		final LocalDateTime endDateTime
	);
}
