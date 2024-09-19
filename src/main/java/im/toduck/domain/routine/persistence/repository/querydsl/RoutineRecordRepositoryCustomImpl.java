package im.toduck.domain.routine.persistence.repository.querydsl;

import java.time.LocalDate;
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
	public List<RoutineRecord> findRoutineRecordsForUserAndDate(User user, LocalDate date) {
		return queryFactory
			.selectFrom(qRecord)
			.join(qRecord.routine, qRoutine)
			.where(
				qRoutine.user.eq(user),
				recordAtBetween(date)
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
}
