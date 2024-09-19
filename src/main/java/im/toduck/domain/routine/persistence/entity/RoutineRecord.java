package im.toduck.domain.routine.persistence.entity;

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
@Getter
@Table(name = "routine_record")
@NoArgsConstructor
public class RoutineRecord extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "routine_id")
	private Routine routine;

	@Column(name = "record_at", nullable = false)
	private LocalDateTime recordAt;

	@Column(name = "is_all_day", nullable = false)
	private Boolean isAllDay;

	@Column(name = "is_completed", nullable = false)
	private Boolean isCompleted = false;

	@Builder
	private RoutineRecord(Routine routine, LocalDateTime recordAt, Boolean isAllDay, Boolean isCompleted) {
		this.routine = routine;
		this.recordAt = recordAt;
		this.isAllDay = isAllDay;
		this.isCompleted = isCompleted;
	}

	public void changeCompletion(Boolean isCompleted) {
		this.isCompleted = isCompleted;
	}
}
