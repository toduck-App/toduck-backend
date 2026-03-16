package im.toduck.domain.schedule.persistence.repository.querydsl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import im.toduck.domain.schedule.persistence.entity.QScheduleReminderJob;
import im.toduck.domain.schedule.persistence.entity.ScheduleReminderJob;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ScheduleReminderJobRepositoryCustomImpl implements ScheduleReminderJobRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QScheduleReminderJob qScheduleReminderJob = QScheduleReminderJob.scheduleReminderJob;

    @Override
    public List<ScheduleReminderJob> findByScheduleId(final Long scheduleId) {
        return queryFactory
                .selectFrom(qScheduleReminderJob)
                .where(qScheduleReminderJob.scheduleId.eq(scheduleId))
                .fetch();
    }

    @Override
    public List<ScheduleReminderJob> findByScheduleIdAndReminderDateGreaterThanEqual(
            final Long scheduleId,
            final LocalDate date) {
        return queryFactory
                .selectFrom(qScheduleReminderJob)
                .where(
                        qScheduleReminderJob.scheduleId.eq(scheduleId),
                        qScheduleReminderJob.reminderDate.goe(date))
                .fetch();
    }

    @Override
    public void deleteByScheduleId(final Long scheduleId) {
        queryFactory
                .delete(qScheduleReminderJob)
                .where(qScheduleReminderJob.scheduleId.eq(scheduleId))
                .execute();
    }

    @Override
    public void deleteByScheduleIdAndReminderDateAfter(final Long scheduleId, final LocalDate date) {
        queryFactory
                .delete(qScheduleReminderJob)
                .where(
                        qScheduleReminderJob.scheduleId.eq(scheduleId),
                        qScheduleReminderJob.reminderDate.goe(date))
                .execute();
    }

    @Override
    public boolean existsByScheduleIdAndReminderDateAndReminderTime(
            final Long scheduleId,
            final LocalDate reminderDate,
            final LocalTime reminderTime) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(qScheduleReminderJob)
                .where(
                        qScheduleReminderJob.scheduleId.eq(scheduleId),
                        qScheduleReminderJob.reminderDate.eq(reminderDate),
                        qScheduleReminderJob.reminderTime.eq(reminderTime))
                .fetchFirst();

        return fetchOne != null;
    }
}
