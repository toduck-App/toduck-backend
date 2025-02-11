package im.toduck.domain.schedule.common.mapper;

import java.time.LocalDate;
import java.util.List;

import im.toduck.domain.routine.persistence.vo.PlanCategoryColor;
import im.toduck.domain.schedule.persistence.entity.Schedule;
import im.toduck.domain.schedule.persistence.entity.ScheduleRecord;
import im.toduck.domain.schedule.persistence.vo.ScheduleDate;
import im.toduck.domain.schedule.persistence.vo.ScheduleTime;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleCreateRequest;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleCreateResponse;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleHeadResponse;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.helper.DaysOfWeekBitmask;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ScheduleMapper {

	public static Schedule toSchedule(final User user, final ScheduleCreateRequest request) {
		DaysOfWeekBitmask daysOfWeekBitmask = null;
		if (request.daysOfWeek() != null) {
			daysOfWeekBitmask = DaysOfWeekBitmask.createByDayOfWeek(request.daysOfWeek());
		}
		PlanCategoryColor planCategoryColor = PlanCategoryColor.from(request.color());
		ScheduleTime scheduleTime = ScheduleTime.from(request.isAllDay(), request.time(), request.alarm());
		ScheduleDate scheduleDate = ScheduleDate.from(request.startDate(), request.endDate());

		return new Schedule(
			request.title(),
			request.category(),
			planCategoryColor,
			scheduleDate,
			scheduleTime, daysOfWeekBitmask,
			request.location(),
			request.memo(),
			user);
	}

	public static ScheduleCreateResponse toScheduleCreateResponse(final Schedule schedule) {
		return ScheduleCreateResponse.builder()
			.scheduleId(schedule.getId())
			.build();
	}

	public static ScheduleHeadResponse toScheduleHeadResponse(LocalDate startDate, LocalDate endDate,
		List<ScheduleHeadResponse.ScheduleHeadDto> scheduleHeadDtos) {
		return ScheduleHeadResponse.builder()
			.queryStartDate(startDate)
			.queryEndDate(endDate)
			.scheduleHeadDtos(scheduleHeadDtos)
			.build();
	}

	public static ScheduleHeadResponse.ScheduleHeadDto toScheduleHeadDto(Schedule schedule,
		List<ScheduleRecord> scheduleRecords) {
		List<ScheduleHeadResponse.ScheduleHeadDto.ScheduleRecordDto> scheduleRecordDtos = scheduleRecords.stream()
			.map(ScheduleHeadResponse.ScheduleHeadDto.ScheduleRecordDto::from)
			.toList();
		return ScheduleHeadResponse.ScheduleHeadDto.builder()
			.scheduleId(schedule.getId())
			.title(schedule.getTitle())
			.scheduleRecordDto(scheduleRecordDtos)
			.color(schedule.getColor().getValue())
			.category(schedule.getCategory())
			.isAllDay(schedule.getScheduleTime().getIsAllDay())
			.startDate(schedule.getScheduleDate().getStartDate())
			.endDate(schedule.getScheduleDate().getEndDate())
			.time(schedule.getScheduleTime().getTime())
			.location(schedule.getLocation())
			.build();
	}
}
