package im.toduck.domain.schedule.persistence.repository.querydsl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import im.toduck.domain.schedule.persistence.entity.QSchedule;
import im.toduck.domain.schedule.persistence.entity.Schedule;
import im.toduck.global.persistence.helper.DailyCountQueryHelper;
import im.toduck.global.persistence.projection.DailyCount;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ScheduleRepositoryCustomImpl implements ScheduleRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QSchedule schedule = QSchedule.schedule;

	@Override
	public List<Schedule> findSchedules(Long userId, LocalDate startDate, LocalDate endDate) {
		return queryFactory
			.selectDistinct(schedule)
			.from(schedule)
			.where(
				schedule.user.id.eq(userId)
					.and(
						isSingleDayNonRepeating(startDate, endDate)
							.or(isSingleDayRepeating(endDate))
							.or(isPeriodEvent(startDate, endDate))
					)
			)
			.fetch();
	}

	// 1. 기간 X, 반복 X: 단일 날짜 일정이며, 반복이 없음
	private BooleanExpression isSingleDayNonRepeating(LocalDate startDate, LocalDate endDate) {
		return schedule.scheduleDate.startDate.eq(schedule.scheduleDate.endDate)
			.and(schedule.daysOfWeekBitmask.isNull())
			.and(schedule.scheduleDate.startDate.between(startDate, endDate));
	}

	// 2. 기간 X, 반복 O: 단일 날짜 일정이지만 반복 일정
	private BooleanExpression isSingleDayRepeating(LocalDate endDate) {
		return schedule.scheduleDate.startDate.eq(schedule.scheduleDate.endDate)
			.and(schedule.daysOfWeekBitmask.isNotNull())
			.and(schedule.scheduleDate.startDate.loe(endDate)); // startDate가 endDate 이전이면 조회 대상
	}

	// 3. 기간 O, 반복 여부 관계없음: 두 기간이 겹치는지 확인
	private BooleanExpression isPeriodEvent(LocalDate startDate, LocalDate endDate) {
		return schedule.scheduleDate.startDate.ne(schedule.scheduleDate.endDate) // 시작일 ≠ 종료일 → 기간 일정
			.and(schedule.scheduleDate.endDate.goe(startDate)) // 조회 시작일이 일정 종료일보다 같거나 작아야 함
			.and(schedule.scheduleDate.startDate.loe(endDate)); // 일정 시작일이 조회 종료일보다 같거나 작아야 함
	}

	@Override
	public List<DailyCount> countByCreatedAtBetweenGroupByDate(
		final LocalDateTime startDateTime,
		final LocalDateTime endDateTime
	) {
		return DailyCountQueryHelper.countGroupByDate(
			queryFactory, schedule, schedule.createdAt, schedule.count(), startDateTime, endDateTime
		);
	}
}
