package im.toduck.domain.schedule.persistence.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import im.toduck.global.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "schedule_reminder_job", uniqueConstraints = {
        @UniqueConstraint(name = "uk_schedule_reminder_date_time", columnNames = { "schedule_id", "reminder_date",
                "reminder_time" })
})
@NoArgsConstructor
public class ScheduleReminderJob extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "reminder_date", nullable = false)
    private LocalDate reminderDate;

    @Column(name = "reminder_time", nullable = false)
    private LocalTime reminderTime;

    @Column(name = "job_key", nullable = false)
    private String jobKey;

    @Builder
    private ScheduleReminderJob(
            final Long scheduleId,
            final Long userId,
            final LocalDate reminderDate,
            final LocalTime reminderTime,
            final String jobKey) {
        this.scheduleId = scheduleId;
        this.userId = userId;
        this.reminderDate = reminderDate;
        this.reminderTime = reminderTime;
        this.jobKey = jobKey;
    }
}
