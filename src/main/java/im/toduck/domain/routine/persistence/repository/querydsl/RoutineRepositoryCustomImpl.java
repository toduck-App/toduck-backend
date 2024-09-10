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
	private final QRoutine routine = QRoutine.routine;

	@Override
	public List<Routine> findUnrecordedRoutinesForDate(User user, LocalDate date,
		List<RoutineRecord> recordedRoutines) {
		return queryFactory
			.selectFrom(routine)
			.where(
				routine.user.eq(user),
				routineCreatedOnOrBeforeDate(date),
				routineNotRecorded(recordedRoutines),
				routineMatchesDate(date)
			)
			.fetch();
	}

	private BooleanExpression routineCreatedOnOrBeforeDate(LocalDate date) {
		LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
		return routine.createdAt.loe(endOfDay);
	}

	private BooleanExpression routineNotRecorded(List<RoutineRecord> recordedRoutines) {
		if (recordedRoutines == null || recordedRoutines.isEmpty()) {
			return null;
		}

		return routine.id.notIn(recordedRoutines.stream()
			.map(RoutineRecord::getRoutine)
			.map(Routine::getId)
			.toList());
	}

	private BooleanExpression routineMatchesDate(LocalDate date) {
		byte dayBitmask = DaysOfWeekBitmask.getDayBitmask(date.getDayOfWeek());
		return Expressions.numberTemplate(
				Byte.class, "function('bitand', {0}, CAST({1} as byte))", routine.daysOfWeekBitmask, dayBitmask
			)

			.gt((byte)0);
	}
}
