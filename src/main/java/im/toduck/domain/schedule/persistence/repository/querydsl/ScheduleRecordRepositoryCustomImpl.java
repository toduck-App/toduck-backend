package im.toduck.domain.schedule.persistence.repository.querydsl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import im.toduck.domain.schedule.persistence.entity.QSchedule;
import im.toduck.domain.schedule.persistence.entity.QScheduleRecord;
import im.toduck.domain.schedule.persistence.entity.ScheduleRecord;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ScheduleRecordRepositoryCustomImpl implements ScheduleRecordRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	private final QScheduleRecord scheduleRecord = QScheduleRecord.scheduleRecord;
	private final QSchedule schedule = QSchedule.schedule;

	@Override
	public List<ScheduleRecord> findByCompletedScheduleAndAfterStartDate(Long scheduleId,
		LocalDate startDate) {
		return queryFactory
			.select(scheduleRecord)
			.from(scheduleRecord)
			.where(
				scheduleRecord.schedule.id.eq(scheduleId)
					.and(scheduleRecord.isCompleted)
					.and(
						scheduleRecord.recordDate.goe(startDate)
					)
			)
			.fetch();
	}

	@Override
	public void deleteByNonCompletedScheduleAndBetweenStartDateAndEndDate(Long scheduleId,
		LocalDate startDate,
		LocalDate endDate) {
		queryFactory
			.delete(scheduleRecord)
			.where(
				scheduleRecord.schedule.id.eq(scheduleId)
					.and(scheduleRecord.isCompleted.isFalse())
					.and(
						scheduleRecord.recordDate.goe(startDate)
					)
			)
			.execute();
	}

	@Override
	public void softDeleteByScheduleIdAndRecordDate(Long id, LocalDate localDate) {
		queryFactory
			.update(scheduleRecord)
			.set(scheduleRecord.deletedAt, LocalDateTime.now())
			.where(
				scheduleRecord.schedule.id.eq(id)
					.and(scheduleRecord.recordDate.eq(localDate))
			)
			.execute();
	}

	@Override
	public void deleteByScheduleIdAndRecordDate(Long id, LocalDate startDate) {
		queryFactory
			.delete(scheduleRecord)
			.where(
				scheduleRecord.schedule.id.eq(id)
					.and(scheduleRecord.recordDate.eq(startDate))
			)
			.execute();
	}

	@Override
	public Optional<ScheduleRecord> findScheduleRecordByUserIdAndRecordDateAndScheduleId(
		LocalDate localDate,
		Long aLong) {
		return Optional.ofNullable(
			queryFactory
				.select(scheduleRecord)
				.from(scheduleRecord)
				.leftJoin(scheduleRecord.schedule, schedule).fetchJoin()
				.where(
					scheduleRecord.schedule.id.eq(aLong)
						.and(scheduleRecord.recordDate.eq(localDate))
				)
				.fetchOne());
	}

	@Override
	public List<ScheduleRecord> findByScheduleAndBetweenStartDateAndEndDate(Long scheduleId, LocalDate startDate,
		LocalDate endDate) {
		return queryFactory
			.select(scheduleRecord)
			.from(scheduleRecord)
			.where(
				scheduleRecord.schedule.id.eq(scheduleId)
					.and(
						scheduleRecord.recordDate.goe(startDate)
							.and(scheduleRecord.recordDate.loe(endDate))
					)
			)
			.fetch();
	}

	@Override
	public Optional<ScheduleRecord> findScheduleRecordFetchJoinSchedule(Long scheduleRecordId) {
		return Optional.ofNullable(
			queryFactory
				.select(scheduleRecord)
				.from(scheduleRecord)
				.leftJoin(scheduleRecord.schedule, schedule).fetchJoin()
				.where(scheduleRecord.id.eq(scheduleRecordId))
				.fetchOne()
		);
	}
}
