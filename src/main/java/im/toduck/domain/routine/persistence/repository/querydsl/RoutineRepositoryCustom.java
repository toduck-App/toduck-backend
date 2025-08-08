package im.toduck.domain.routine.persistence.repository.querydsl;

import java.time.LocalDate;
import java.util.List;

import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.entity.RoutineRecord;
import im.toduck.domain.user.persistence.entity.User;

public interface RoutineRepositoryCustom {
	List<Routine> findUnrecordedRoutinesByDateMatchingDayOfWeek(
		final User user,
		final LocalDate date,
		final List<RoutineRecord> routineRecords
	);

	List<Routine> findRoutinesByDateBetween(
		final User user,
		final LocalDate startDate,
		final LocalDate endDate
	);

	boolean isActiveForDate(final Routine routine, final LocalDate date);

	void deleteAllUnsharedRoutinesByUser(User user);

	List<Routine> findActiveRoutinesWithReminderForDates(LocalDate startDate, LocalDate endDate);
}
