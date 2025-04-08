package im.toduck.domain.routine.persistence.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

import org.hibernate.annotations.ColumnDefault;

import im.toduck.domain.person.persistence.entity.PlanCategory;
import im.toduck.domain.routine.common.converter.DaysOfWeekBitmaskConverter;
import im.toduck.domain.routine.persistence.vo.PlanCategoryColor;
import im.toduck.domain.routine.persistence.vo.RoutineMemo;
import im.toduck.domain.routine.presentation.dto.request.RoutineUpdateRequest;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.base.entity.BaseEntity;
import im.toduck.global.helper.DaysOfWeekBitmask;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "routine")
@NoArgsConstructor
public class Routine extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// TODO: 변경 필요
	@Enumerated(EnumType.STRING)
	private PlanCategory category;

	// TODO: 변경 필요
	@Embedded
	private PlanCategoryColor color;

	@Column(nullable = false, columnDefinition = "CHAR(100)")
	private String title;

	@Column(nullable = false)
	private Boolean isPublic;

	@Column(name = "reminder_minutes")
	private Integer reminderMinutes;

	@Embedded
	private RoutineMemo memo;

	@Column
	private LocalTime time;

	@Column(name = "schedule_modified_at")
	private LocalDateTime scheduleModifiedAt;

	@Convert(converter = DaysOfWeekBitmaskConverter.class)
	@Column(name = "days_of_week", nullable = false)
	private DaysOfWeekBitmask daysOfWeekBitmask;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Builder
	private Routine(
		PlanCategory category,
		PlanCategoryColor color,
		String title,
		Boolean isPublic,
		Integer reminderMinutes,
		RoutineMemo memo,
		LocalTime time,
		DaysOfWeekBitmask daysOfWeekBitmask,
		User user
	) {
		this.category = category;
		this.color = color;
		this.title = title;
		this.isPublic = isPublic;
		this.reminderMinutes = reminderMinutes;
		this.memo = memo;
		this.time = time;
		this.daysOfWeekBitmask = daysOfWeekBitmask;
		this.user = user;
		this.scheduleModifiedAt = LocalDateTime.now();
	}

	public String getColorValue() {
		return color.getValue();
	}

	public String getMemoValue() {
		if (memo == null) {
			return null;
		}
		return memo.getValue();
	}

	public void updateFromRequest(RoutineUpdateRequest request) {
		if (request.isTitleChanged()) {
			this.title = request.title();
		}

		if (request.isCategoryChanged()) {
			this.category = request.category();
		}

		if (request.isColorChanged()) {
			this.color = PlanCategoryColor.from(request.color());
		}

		if (request.isTimeChanged() && !Objects.equals(this.time, request.time())) {
			this.time = request.time();
			this.scheduleModifiedAt = LocalDateTime.now();
		}

		if (request.isPublicChanged()) {
			this.isPublic = request.isPublic();
		}

		if (request.isDaysOfWeekChanged()) {
			DaysOfWeekBitmask newDaysOfWeek = DaysOfWeekBitmask.createByDayOfWeek(request.daysOfWeek());
			if (!newDaysOfWeek.equals(this.daysOfWeekBitmask)) {
				this.daysOfWeekBitmask = newDaysOfWeek;
				this.scheduleModifiedAt = LocalDateTime.now();
			}
		}

		if (request.isReminderMinutesChanged()) {
			this.reminderMinutes = request.reminderMinutes();
		}

		if (request.isMemoChanged()) {
			this.memo = RoutineMemo.from(request.memo());
		}
	}

	public void delete() {
		this.deletedAt = LocalDateTime.now();
	}

	public Boolean isInDeletedState() {
		return deletedAt != null;
	}

	public Boolean isAllDay() {
		return time == null;
	}
}
