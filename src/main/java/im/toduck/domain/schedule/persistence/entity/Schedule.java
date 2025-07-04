package im.toduck.domain.schedule.persistence.entity;

import static jakarta.persistence.CascadeType.*;
import static java.util.Objects.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import im.toduck.domain.person.persistence.entity.PlanCategory;
import im.toduck.domain.routine.persistence.vo.PlanCategoryColor;
import im.toduck.domain.schedule.common.converter.ScheduleDaysOfWeekBitmaskConverter;
import im.toduck.domain.schedule.persistence.vo.ScheduleDate;
import im.toduck.domain.schedule.persistence.vo.ScheduleTime;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleCompleteRequest;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleCreateRequest;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "schedule")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

	@Embedded
	private ScheduleDate scheduleDate;

	@Embedded
	private ScheduleTime scheduleTime;

	@Convert(converter = ScheduleDaysOfWeekBitmaskConverter.class)
	@Column(name = "days_of_week", nullable = true)
	private DaysOfWeekBitmask daysOfWeekBitmask;

	@Column(nullable = true, length = 255)
	private String location;

	@Column(nullable = true, length = 255)
	private String memo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@OneToMany(mappedBy = "schedule", fetch = FetchType.LAZY, cascade = {PERSIST, MERGE})
	private List<ScheduleRecord> scheduleRecords = new ArrayList<>();

	@Builder
	public Schedule(String title,
		PlanCategory category,
		PlanCategoryColor color,
		ScheduleDate scheduleDate,
		ScheduleTime scheduleTime,
		DaysOfWeekBitmask daysOfWeekBitmask,
		String location,
		String memo,
		User user) {
		this.title = title;
		this.category = category;
		this.color = color;
		this.scheduleTime = scheduleTime;
		this.scheduleDate = scheduleDate;
		this.daysOfWeekBitmask = daysOfWeekBitmask;
		this.location = location;
		this.memo = memo;
		this.user = user;
	}

	public static Schedule create(User user, ScheduleCreateRequest request) {
		Schedule schedule = new Schedule();

		modifyInfo(schedule, request);
		schedule.user = requireNonNull(user);

		return schedule;
	}

	public void completeSchedule(ScheduleCompleteRequest request) {
		ScheduleRecord scheduleRecord = this.scheduleRecords.stream()
			.filter(sr -> isCompleteRecordTarget(request.queryDate(), sr))
			.findFirst()
			.orElseGet(() -> {
				ScheduleRecord create = ScheduleRecord.create(this, request.queryDate());
				this.scheduleRecords.add(create);
				return create;
			});

		scheduleRecord.changeComplete(request.isComplete());
	}

	private static boolean isCompleteRecordTarget(LocalDate queryDate, ScheduleRecord sr) {
		return sr.getRecordDate().equals(queryDate) && sr.getDeletedAt() == null;
	}

	private boolean isSingleNonRepeatableSchedule() {
		return this.getScheduleDate().getStartDate().equals(this.getScheduleDate().getEndDate())
			&& this.daysOfWeekBitmask == null;
	}

	public void minusOneQueryDate(LocalDate queryDate) {
		this.scheduleDate = ScheduleDate.of(this.getScheduleDate().getStartDate(), queryDate.minusDays(1));
	}

	public void updateInfo(ScheduleCreateRequest request) {
		modifyInfo(this, request);
	}

	private static void modifyInfo(Schedule schedule, ScheduleCreateRequest request) {
		DaysOfWeekBitmask daysOfWeekBitmask = null;
		if (request.daysOfWeek() != null) {
			daysOfWeekBitmask = DaysOfWeekBitmask.createByDayOfWeek(request.daysOfWeek());
		}
		schedule.title = request.title();
		schedule.category = request.category();
		schedule.color = PlanCategoryColor.from(request.color());
		schedule.scheduleDate = ScheduleDate.of(request.startDate(), request.endDate());
		schedule.scheduleTime = ScheduleTime.of(request.isAllDay(), request.time(), request.alarm());
		schedule.daysOfWeekBitmask = daysOfWeekBitmask;
		schedule.location = request.location();
		schedule.memo = request.memo();
	}
}
