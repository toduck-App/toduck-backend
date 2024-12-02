package im.toduck.domain.schedule.persistence.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import im.toduck.domain.person.persistence.entity.PlanCategory;
import im.toduck.domain.routine.common.converter.DaysOfWeekBitmaskConverter;
import im.toduck.domain.schedule.persistence.vo.ScheduleAlram;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.base.entity.BaseEntity;
import im.toduck.global.helper.DaysOfWeekBitmask;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "schedule")
@Getter
@NoArgsConstructor
public class Schedule extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String title;

	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private PlanCategory category;

	@Column(nullable = true, length = 100)
	private String categoryColor;

	@Column(nullable = false)
	private LocalDate startDate;

	@Column(nullable = false)
	private LocalDate endDate;

	@Column(nullable = true)
	private LocalTime time;

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
}
