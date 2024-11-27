package im.toduck.domain.routine.persistence.repository.querydsl;

import java.time.LocalDate;
import java.util.List;

import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.entity.RoutineRecord;
import im.toduck.domain.user.persistence.entity.User;

public interface RoutineRepositoryCustom {
	List<Routine> findUnrecordedRoutinesForDate(
		final User user,
		final LocalDate date,
		final List<RoutineRecord> routineRecords
	);

	boolean isActiveForDate(final Routine routine, final LocalDate date);

	void softDelete(final Routine routine);
}
