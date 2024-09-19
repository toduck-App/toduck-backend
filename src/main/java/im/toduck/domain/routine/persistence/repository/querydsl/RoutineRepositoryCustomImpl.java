package im.toduck.domain.routine.persistence.repository.querydsl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import im.toduck.domain.routine.persistence.entity.QRoutine;
import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.entity.RoutineRecord;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.helper.DaysOfWeekBitmask;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RoutineRepositoryCustomImpl implements RoutineRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	private final QRoutine qRoutine = QRoutine.routine;

	@Override
	public List<Routine> findUnrecordedRoutinesForDate(
		final User user,
		final LocalDate date,
		final List<RoutineRecord> routineRecords
	) {
		return queryFactory
			.selectFrom(qRoutine)
			.where(
				qRoutine.user.eq(user),
				routineCreatedOnOrBeforeDate(date),
				routineNotRecorded(routineRecords),
				routineMatchesDate(date)
			)
			.fetch();
	}

	@Override
	public boolean isActiveForDate(Routine routine, LocalDate date) {
		Integer fetchOne = queryFactory
			.selectOne()
			.from(qRoutine)
			.where(
				qRoutine.eq(routine),
				routineCreatedOnOrBeforeDate(date),
				routineMatchesDate(date)
			)
			.fetchFirst();

		return fetchOne != null;
	}

	private BooleanExpression routineCreatedOnOrBeforeDate(LocalDate date) {
		LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
		return qRoutine.createdAt.loe(endOfDay);
	}

	private BooleanExpression routineNotRecorded(List<RoutineRecord> recordedRoutines) {
		if (recordedRoutines == null || recordedRoutines.isEmpty()) {
			return null;
		}

		return qRoutine.id.notIn(recordedRoutines.stream()
			.map(RoutineRecord::getRoutine)
			.map(Routine::getId)
			.toList());
	}

	private BooleanExpression routineMatchesDate(LocalDate date) {
		byte dayBitmask = DaysOfWeekBitmask.getDayBitmask(date.getDayOfWeek());
		return Expressions.numberTemplate(
			Byte.class, "function('bitand', {0}, CAST({1} as byte))", qRoutine.daysOfWeekBitmask, dayBitmask
		).gt((byte)0);
	}
}
