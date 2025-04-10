package im.toduck.domain.routine.persistence.repository.querydsl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.entity.RoutineRecord;
import im.toduck.domain.user.persistence.entity.User;

public interface RoutineRecordRepositoryCustom {
	List<RoutineRecord> findRoutineRecordsForUserAndDate(final User user, final LocalDate date);

	Optional<RoutineRecord> findByRoutineAndRecordDate(
		final Routine routine,
		final LocalDate date
	);

	void deleteIncompletedFuturesByRoutine(final Routine routine, final LocalDateTime targetDateTime);

	List<RoutineRecord> findAllByRoutineAndRecordAtBetween(
		final Routine routine,
		final LocalDateTime startTime,
		final LocalDateTime endTime
	);
}
