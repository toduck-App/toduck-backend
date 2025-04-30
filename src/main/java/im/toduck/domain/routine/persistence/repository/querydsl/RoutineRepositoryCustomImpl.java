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
	public List<Routine> findUnrecordedRoutinesByDateMatchingDayOfWeek(
		final User user,
		final LocalDate date,
		final List<RoutineRecord> routineRecords
	) {
		return queryFactory
			.selectFrom(qRoutine)
			.where(
				qRoutine.user.eq(user),
				scheduleModifiedOnOrBeforeDate(date),
				routineNotRecorded(routineRecords),
				routineMatchesDate(date),
				routineNotDeleted()
			)
			.fetch();
	}

	@Override
	public List<Routine> findRoutinesByDateBetween(
		final User user,
		final LocalDate startDate,
		final LocalDate endDate
	) {
		return queryFactory
			.selectFrom(qRoutine)
			.where(
				qRoutine.user.eq(user),
				routineNotDeleted(),
				scheduleModifiedOnOrBeforeDate(endDate),
				routineMatchesDateRange(startDate, endDate)
			)
			.fetch();
	}

	private BooleanExpression routineNotRecorded(final List<RoutineRecord> recordedRoutines) {
		if (recordedRoutines == null || recordedRoutines.isEmpty()) {
			return null;
		}

		return qRoutine.id.notIn(recordedRoutines.stream()
			.map(RoutineRecord::getRoutine)
			.map(Routine::getId)
			.toList());
	}

	@Override
	public boolean isActiveForDate(final Routine routine, final LocalDate date) {
		Integer fetchOne = queryFactory
			.selectOne()
			.from(qRoutine)
			.where(
				qRoutine.eq(routine),
				scheduleModifiedOnOrBeforeDate(date),
				routineMatchesDate(date),
				routineNotDeleted()
			)
			.fetchFirst();

		return fetchOne != null;
	}

	private BooleanExpression routineNotDeleted() {
		return qRoutine.deletedAt.isNull();
	}

	private BooleanExpression scheduleModifiedOnOrBeforeDate(final LocalDate date) {
		LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
		return qRoutine.scheduleModifiedAt.loe(endOfDay);
	}

	private BooleanExpression routineMatchesDate(final LocalDate date) {
		byte dayBitmask = DaysOfWeekBitmask.getDayBitmask(date.getDayOfWeek());

		return Expressions.numberTemplate(
			Byte.class, "function('bitand', {0}, CAST({1} as byte))", qRoutine.daysOfWeekBitmask, dayBitmask
		).gt((byte)0);
	}

	/**
	 * 루틴의 요일이 시작일부터 종료일까지의 기간 내 요일과 일치하는지 확인하는 조건
	 */
	private BooleanExpression routineMatchesDateRange(final LocalDate startDate, final LocalDate endDate) {
		byte periodDaysBitmask = DaysOfWeekBitmask.getDaysOfWeekBitmaskValueInRange(startDate, endDate);

		return Expressions.numberTemplate(
			Byte.class, "function('bitand', {0}, CAST({1} as byte))", qRoutine.daysOfWeekBitmask, periodDaysBitmask
		).gt((byte)0);
	}
}
