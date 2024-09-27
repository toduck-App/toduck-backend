package im.toduck.domain.routine.common.mapper;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.entity.RoutineRecord;
import im.toduck.domain.routine.persistence.vo.PlanCategoryColor;
import im.toduck.domain.routine.persistence.vo.RoutineMemo;
import im.toduck.domain.routine.presentation.dto.request.RoutineCreateRequest;
import im.toduck.domain.routine.presentation.dto.response.MyRoutineReadListResponse;
import im.toduck.domain.routine.presentation.dto.response.RoutineCreateResponse;
import im.toduck.domain.routine.presentation.dto.response.RoutineDetailResponse;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.helper.DaysOfWeekBitmask;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class RoutineMapper {
	private static final boolean INCOMPLETE_STATUS = false;

	public static Routine toRoutine(final User user, final RoutineCreateRequest request) {
		DaysOfWeekBitmask daysOfWeekBitmask = DaysOfWeekBitmask.createByDayOfWeek(request.daysOfWeek());
		PlanCategoryColor planCategoryColor = PlanCategoryColor.from(request.color());
		RoutineMemo routineMemo = RoutineMemo.from(request.memo());

		return Routine.builder()
			.user(user)
			.category(request.category())
			.color(planCategoryColor)
			.title(request.title())
			.memo(routineMemo)
			.isPublic(request.isPublic())
			.reminderMinutes(request.reminderMinutes())
			.time(request.time())
			.daysOfWeekBitmask(daysOfWeekBitmask)
			.build();
	}

	public static RoutineCreateResponse toRoutineCreateResponse(final Routine routine) {
		return RoutineCreateResponse.builder()
			.routineId(routine.getId())
			.build();
	}

	public static MyRoutineReadListResponse toMyRoutineReadResponse(
		final LocalDate queryDate,
		final List<Routine> routines,
		final List<RoutineRecord> routineRecords
	) {
		List<MyRoutineReadListResponse.MyRoutineReadResponse> routineResponses = routines.stream()
			.map(routine -> toMyRoutineReadResponse(routine, INCOMPLETE_STATUS))
			.toList();

		List<MyRoutineReadListResponse.MyRoutineReadResponse> recordResponses = routineRecords.stream()
			.map(record -> toMyRoutineReadResponse(record.getRoutine(), record.getIsCompleted()))
			.toList();

		List<MyRoutineReadListResponse.MyRoutineReadResponse> combinedResponses =
			Stream.concat(routineResponses.stream(), recordResponses.stream()).toList();

		return MyRoutineReadListResponse.builder()
			.queryDate(queryDate)
			.routines(combinedResponses)
			.build();
	}

	private static MyRoutineReadListResponse.MyRoutineReadResponse toMyRoutineReadResponse(
		final Routine routine,
		final boolean isCompleted
	) {
		return MyRoutineReadListResponse.MyRoutineReadResponse.builder()
			.routineId(routine.getId())
			.color(routine.getColorValue())
			.time(routine.getTime())
			.title(routine.getTitle())
			.isCompleted(isCompleted)
			.build();
	}

	public static RoutineDetailResponse toRoutineDetailResponse(final Routine routine) {
		DaysOfWeekBitmask daysOfWeekBitmask = routine.getDaysOfWeekBitmask();
		List<DayOfWeek> daysOfWeek = daysOfWeekBitmask.getDaysOfWeek().stream()
			.sorted()
			.toList();

		return RoutineDetailResponse.builder()
			.routineId(routine.getId())
			.category(routine.getCategory())
			.color(routine.getColorValue())
			.title(routine.getTitle())
			.time(routine.getTime())
			.daysOfWeek(daysOfWeek)
			.memo(routine.getMemoValue())
			.build();
	}
}
