package im.toduck.domain.routine.persistence.repository.querydsl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import im.toduck.domain.routine.persistence.entity.RoutineReminderJob;

public interface RoutineReminderJobRepositoryCustom {

	List<RoutineReminderJob> findByRoutineId(Long routineId);

	List<RoutineReminderJob> findByRoutineIdAndReminderDateGreaterThanEqual(Long routineId, LocalDate date);

	void deleteByRoutineId(Long routineId);

	void deleteByRoutineIdAndReminderDateAfter(Long routineId, LocalDate date);

	boolean existsByRoutineIdAndReminderDateAndReminderTime(Long routineId, LocalDate reminderDate,
		LocalTime reminderTime);

	List<String> findJobKeysByRoutineId(Long routineId);
}
