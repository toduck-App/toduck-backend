package im.toduck.domain.schedule.persistence.entity;

import java.time.LocalDate;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import im.toduck.domain.person.persistence.entity.PlanCategory;
import im.toduck.domain.routine.common.converter.DaysOfWeekBitmaskConverter;
import im.toduck.domain.routine.persistence.vo.PlanCategoryColor;
import im.toduck.domain.schedule.persistence.vo.ScheduleAlram;
import im.toduck.domain.schedule.persistence.vo.ScheduleTime;
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
@Table(name = "schedule")
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE schedule SET deleted_at = NOW() where id=?")
@SQLRestriction(value = "deleted_at is NULL")
public class Schedule extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String title;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PlanCategory category;

	@Embedded
	private PlanCategoryColor color;

	@Column(nullable = false)
	private LocalDate startDate;

	@Column(nullable = false)
	private LocalDate endDate;

	@Embedded
	private ScheduleTime scheduleTime;

	@Convert(converter = DaysOfWeekBitmaskConverter.class)
	@Column(name = "days_of_week", nullable = true)
	private DaysOfWeekBitmask daysOfWeekBitmask;

	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private ScheduleAlram alarm;

	@Column(nullable = true, length = 255)
	private String location;

	@Column(nullable = true, length = 255)
	private String memo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Builder
	public Schedule(String title, PlanCategory category, PlanCategoryColor color, LocalDate startDate,
		LocalDate endDate,
		ScheduleTime scheduleTime, DaysOfWeekBitmask daysOfWeekBitmask, ScheduleAlram alarm, String location,
		String memo, User user) {
		this.title = title;
		this.category = category;
		this.color = color;
		this.startDate = startDate;
		this.endDate = endDate;
		this.scheduleTime = scheduleTime;
		this.daysOfWeekBitmask = daysOfWeekBitmask;
		this.alarm = alarm;
		this.location = location;
		this.memo = memo;
		this.user = user;
	}
}
