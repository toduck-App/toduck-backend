package im.toduck.domain.routine.persistence.entity;

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
@Table(
	name = "routine_reminder_job",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_routine_reminder_date_time", columnNames = {"routine_id", "reminder_date", "reminder_time"})
	}
)
@NoArgsConstructor
public class RoutineReminderJob extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "routine_id", nullable = false)
	private Long routineId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "reminder_date", nullable = false)
	private LocalDate reminderDate;

	@Column(name = "reminder_time", nullable = false)
	private LocalTime reminderTime;

	@Column(name = "job_key", nullable = false)
	private String jobKey;

	@Builder
	private RoutineReminderJob(
		Long routineId,
		Long userId,
		LocalDate reminderDate,
		LocalTime reminderTime,
		String jobKey
	) {
		this.routineId = routineId;
		this.userId = userId;
		this.reminderDate = reminderDate;
		this.reminderTime = reminderTime;
		this.jobKey = jobKey;
	}
}
