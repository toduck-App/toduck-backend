package im.toduck.domain.schedule.presentation.dto.request;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import im.toduck.domain.person.persistence.entity.PlanCategory;
import im.toduck.domain.schedule.persistence.vo.ScheduleAlram;
import lombok.Builder;

@Builder
public record ScheduleCreateRequest(
	String title,
	PlanCategory category,
	String categoryColor,
	LocalTime time,
	ScheduleAlram alarm,
	LocalDate startDate,
	LocalDate endDate,
	DayOfWeek repeatDayOfWeek,
	String location,
	String memo
) {
}
