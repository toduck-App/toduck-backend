package im.toduck.domain.schedule.common.mapper;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import im.toduck.domain.routine.persistence.vo.PlanCategoryColor;
import im.toduck.domain.schedule.persistence.entity.Schedule;
import im.toduck.domain.schedule.persistence.entity.ScheduleRecord;
import im.toduck.domain.schedule.persistence.vo.ScheduleDate;
import im.toduck.domain.schedule.persistence.vo.ScheduleTime;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleCreateRequest;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleHeadResponse;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleIdResponse;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleInfoResponse;
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
		ScheduleTime scheduleTime = ScheduleTime.of(request.isAllDay(), request.time(), request.alarm());
		ScheduleDate scheduleDate = ScheduleDate.of(request.startDate(), request.endDate());

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

	public static ScheduleIdResponse toScheduleIdResponse(final Schedule schedule) {
		return ScheduleIdResponse.builder()
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
			.daysOfWeek(convertDaysOfWeekBitmaskToDayOfWeekList(schedule.getDaysOfWeekBitmask()))
			.time(schedule.getScheduleTime().getTime())
			.location(schedule.getLocation())
			.memo(schedule.getMemo())
			.build();
	}

	public static ScheduleInfoResponse toScheduleInfoResponse(ScheduleRecord scheduleRecord) {
		Schedule schedule = scheduleRecord.getSchedule();
		return ScheduleInfoResponse.builder()
			.scheduleId(schedule.getId())
			.title(schedule.getTitle())
			.color(schedule.getColor().getValue())
			.category(schedule.getCategory())
			.isAllDay(schedule.getScheduleTime().getIsAllDay())
			.startDate(schedule.getScheduleDate().getStartDate())
			.endDate(schedule.getScheduleDate().getEndDate())
			.daysOfWeek(convertDaysOfWeekBitmaskToDayOfWeekList(schedule.getDaysOfWeekBitmask()))
			.time(schedule.getScheduleTime().getTime())
			.location(schedule.getLocation())
			.memo(schedule.getMemo())
			.scheduleRecordId(scheduleRecord.getId())
			.isComplete(scheduleRecord.getIsCompleted())
			.recordDate(scheduleRecord.getRecordDate())
			.deletedAt(scheduleRecord.getDeletedAt())
			.build();
	}

	private static List<DayOfWeek> convertDaysOfWeekBitmaskToDayOfWeekList(DaysOfWeekBitmask daysOfWeekBitmask) {
		if (daysOfWeekBitmask == null) {
			return null;
		}
		return daysOfWeekBitmask.getDaysOfWeek().stream().toList();
	}

	public static Schedule copyToSchedule(Schedule schedule, LocalDate queryDate) {
		ScheduleDate from = ScheduleDate.of(queryDate, queryDate);

		return new Schedule(
			schedule.getTitle(),
			schedule.getCategory(),
			schedule.getColor(),
			from,
			schedule.getScheduleTime(),
			schedule.getDaysOfWeekBitmask(),
			schedule.getLocation(),
			schedule.getMemo(),
			schedule.getUser()
		);
	}
}
