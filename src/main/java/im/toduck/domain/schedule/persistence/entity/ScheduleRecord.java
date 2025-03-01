package im.toduck.domain.schedule.persistence.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import im.toduck.global.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "schedule_record")
@Getter
@NoArgsConstructor
public class ScheduleRecord extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Boolean isCompleted;

	@Column(nullable = false)
	private LocalDate recordDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schedule_id", nullable = false)
	private Schedule schedule;

	@Builder
	private ScheduleRecord(Boolean isCompleted, LocalDate recordDate, Schedule schedule) {
		this.isCompleted = isCompleted;
		this.recordDate = recordDate;
		this.schedule = schedule;
	}

	public void changeComplete(Boolean complete) {
		this.isCompleted = complete;
	}

	public void changeSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public void softDelete() {
		super.deletedAt = LocalDateTime.now();
	}
}
