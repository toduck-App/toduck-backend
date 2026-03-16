package im.toduck.domain.schedule.persistence.repository.querydsl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import im.toduck.domain.schedule.persistence.entity.ScheduleReminderJob;

public interface ScheduleReminderJobRepositoryCustom {

    List<ScheduleReminderJob> findByScheduleId(final Long scheduleId);

    List<ScheduleReminderJob> findByScheduleIdAndReminderDateGreaterThanEqual(final Long scheduleId,
            final LocalDate date);

    void deleteByScheduleId(final Long scheduleId);

    void deleteByScheduleIdAndReminderDateAfter(final Long scheduleId, final LocalDate date);

    boolean existsByScheduleIdAndReminderDateAndReminderTime(final Long scheduleId, final LocalDate reminderDate,
            final LocalTime reminderTime);
}
