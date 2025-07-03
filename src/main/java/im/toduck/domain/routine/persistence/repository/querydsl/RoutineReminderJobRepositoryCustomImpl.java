package im.toduck.domain.routine.persistence.repository.querydsl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import im.toduck.domain.routine.persistence.entity.QRoutineReminderJob;
import im.toduck.domain.routine.persistence.entity.RoutineReminderJob;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RoutineReminderJobRepositoryCustomImpl implements RoutineReminderJobRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QRoutineReminderJob qRoutineReminderJob = QRoutineReminderJob.routineReminderJob;

	@Override
	public List<RoutineReminderJob> findByRoutineId(Long routineId) {
		return queryFactory
			.selectFrom(qRoutineReminderJob)
			.where(qRoutineReminderJob.routineId.eq(routineId))
			.fetch();
	}

	@Override
	public List<RoutineReminderJob> findByRoutineIdAndReminderDateGreaterThanEqual(Long routineId, LocalDate date) {
		return queryFactory
			.selectFrom(qRoutineReminderJob)
			.where(
				qRoutineReminderJob.routineId.eq(routineId),
				qRoutineReminderJob.reminderDate.goe(date)
			)
			.fetch();
	}

	@Override
	public void deleteByRoutineId(Long routineId) {
		queryFactory
			.delete(qRoutineReminderJob)
			.where(qRoutineReminderJob.routineId.eq(routineId))
			.execute();
	}

	@Override
	public void deleteByRoutineIdAndReminderDateAfter(Long routineId, LocalDate date) {
		queryFactory
			.delete(qRoutineReminderJob)
			.where(
				qRoutineReminderJob.routineId.eq(routineId),
				qRoutineReminderJob.reminderDate.goe(date)
			)
			.execute();
	}

	@Override
	public boolean existsByRoutineIdAndReminderDateAndReminderTime(
		Long routineId,
		LocalDate reminderDate,
		LocalTime reminderTime
	) {
		Integer fetchOne = queryFactory
			.selectOne()
			.from(qRoutineReminderJob)
			.where(
				qRoutineReminderJob.routineId.eq(routineId),
				qRoutineReminderJob.reminderDate.eq(reminderDate),
				qRoutineReminderJob.reminderTime.eq(reminderTime)
			)
			.fetchFirst();

		return fetchOne != null;
	}

	@Override
	public List<String> findJobKeysByRoutineId(Long routineId) {
		return queryFactory
			.select(qRoutineReminderJob.jobKey)
			.distinct()
			.from(qRoutineReminderJob)
			.where(qRoutineReminderJob.routineId.eq(routineId))
			.fetch();
	}
}
