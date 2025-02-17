package im.toduck.domain.schedule.persistence.repository.querydsl;

import java.time.LocalDate;
import java.util.List;

import im.toduck.domain.schedule.persistence.entity.Schedule;

public interface ScheduleRepositoryCustom {
	List<Schedule> findSchedules(Long userId, LocalDate startDate, LocalDate endDate);
}
