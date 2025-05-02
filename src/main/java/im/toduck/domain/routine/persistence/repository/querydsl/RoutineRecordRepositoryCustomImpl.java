package im.toduck.domain.routine.persistence.repository.querydsl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import im.toduck.domain.routine.persistence.entity.QRoutine;
import im.toduck.domain.routine.persistence.entity.QRoutineRecord;
import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.entity.RoutineRecord;
import im.toduck.domain.user.persistence.entity.User;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RoutineRecordRepositoryCustomImpl implements RoutineRecordRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	private final QRoutine qRoutine = QRoutine.routine;
	private final QRoutineRecord qRecord = QRoutineRecord.routineRecord;

	@Override
	public List<RoutineRecord> findAllByUserAndRecordAtDate(User user, LocalDate date) {
		return queryFactory
			.selectFrom(qRecord)
			.join(qRecord.routine, qRoutine).fetchJoin()
			.where(
				qRoutine.user.eq(user),
				recordAtBetween(date)
			)
			.fetch();
	}

	@Override
	public List<RoutineRecord> findAllByUserAndRecordAtBetween(User user, LocalDate startDate, LocalDate endDate) {
		return queryFactory
			.selectFrom(qRecord)
			.join(qRecord.routine, qRoutine).fetchJoin()
			.where(
				qRoutine.user.eq(user),
				recordAtBetweenDates(startDate, endDate)
			)
			.fetch();
	}

	@Override
	public Optional<RoutineRecord> findByRoutineAndRecordDate(
		final Routine routine,
		final LocalDate date
	) {
		RoutineRecord result = queryFactory
			.selectFrom(qRecord)
			.where(
				qRecord.routine.eq(routine),
				recordAtBetween(date)
			)
			.fetchOne();

		return Optional.ofNullable(result);
	}

	private BooleanExpression recordAtBetween(LocalDate date) {
		return qRecord.recordAt.between(
			date.atStartOfDay(),
			date.plusDays(1).atStartOfDay().minusNanos(1)
		);
	}

	private BooleanExpression recordAtBetweenDates(LocalDate startDate, LocalDate endDate) {
		return qRecord.recordAt.between(
			startDate.atStartOfDay(),
			endDate.plusDays(1).atStartOfDay().minusNanos(1)
		);
	}

	@Override
	public void deleteIncompletedFuturesByRoutine(final Routine routine, final LocalDateTime targetDateTime) {
		queryFactory
			.delete(qRecord)
			.where(
				qRecord.routine.eq(routine),
				qRecord.recordAt.after(targetDateTime),
				qRecord.isCompleted.isFalse(),
				qRecord.deletedAt.isNull()
			)
			.execute();
	}

	@Override
	public List<RoutineRecord> findAllByRoutineAndRecordAtBetween(
		final Routine routine,
		final LocalDateTime startTime,
		final LocalDateTime endTime
	) {
		return queryFactory
			.selectFrom(qRecord)
			.where(
				qRecord.routine.eq(routine),
				qRecord.recordAt.after(startTime),
				qRecord.recordAt.before(endTime)
			)
			.fetch();
	}
}
