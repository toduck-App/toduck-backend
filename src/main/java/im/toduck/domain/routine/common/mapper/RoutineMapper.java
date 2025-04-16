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
import im.toduck.domain.routine.presentation.dto.response.MyRoutineAvailableListResponse;
import im.toduck.domain.routine.presentation.dto.response.MyRoutineRecordReadListResponse;
import im.toduck.domain.routine.presentation.dto.response.RoutineCreateResponse;
import im.toduck.domain.routine.presentation.dto.response.RoutineDetailResponse;
import im.toduck.domain.social.presentation.dto.response.UserProfileRoutineListResponse;
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

	public static MyRoutineRecordReadListResponse toMyRoutineRecordReadListResponse(
		final LocalDate queryDate,
		final List<Routine> routines,
		final List<RoutineRecord> routineRecords
	) {
		List<MyRoutineRecordReadListResponse.MyRoutineReadResponse> routineResponses = routines.stream()
			.map(routine -> toMyRoutineRecordReadResponse(routine, INCOMPLETE_STATUS))
			.toList();

		List<MyRoutineRecordReadListResponse.MyRoutineReadResponse> recordResponses = routineRecords.stream()
			.map(record -> toMyRoutineRecordReadResponse(record.getRoutine(), record.getIsCompleted()))
			.toList();

		List<MyRoutineRecordReadListResponse.MyRoutineReadResponse> combinedResponses =
			Stream.concat(routineResponses.stream(), recordResponses.stream()).toList();

		return MyRoutineRecordReadListResponse.builder()
			.queryDate(queryDate)
			.routines(combinedResponses)
			.build();
	}

	private static MyRoutineRecordReadListResponse.MyRoutineReadResponse toMyRoutineRecordReadResponse(
		final Routine routine,
		final boolean isCompleted
	) {
		return MyRoutineRecordReadListResponse.MyRoutineReadResponse.builder()
			.routineId(routine.getId())
			.color(routine.getColorValue())
			.category(routine.getCategory())
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
			.memo(routine.getMemoValue())
			.time(routine.getTime())
			.isPublic(routine.getIsPublic())
			.isInDeletedState(routine.isInDeletedState())
			.daysOfWeek(daysOfWeek)
			.build();
	}

	public static MyRoutineAvailableListResponse toMyRoutineAvailableListResponse(final List<Routine> routines) {
		List<MyRoutineAvailableListResponse.MyRoutineAvailableResponse> routineResponses = routines.stream()
			.map(RoutineMapper::toMyRoutineRecordReadResponse)
			.toList();

		return MyRoutineAvailableListResponse.builder()
			.routines(routineResponses)
			.build();
	}

	private static MyRoutineAvailableListResponse.MyRoutineAvailableResponse toMyRoutineRecordReadResponse(
		final Routine routine
	) {
		return MyRoutineAvailableListResponse.MyRoutineAvailableResponse.builder()
			.routineId(routine.getId())
			.category(routine.getCategory())
			.color(routine.getColorValue())
			.title(routine.getTitle())
			.memo(routine.getMemoValue())
			.build();
	}

	public static UserProfileRoutineListResponse toUserProfileRoutineListResponse(final List<Routine> routines) {
		List<UserProfileRoutineListResponse.UserProfileRoutineResponse> routineResponses = routines.stream()
			.map(RoutineMapper::toUserProfileRoutineRecordReadResponse)
			.toList();

		return UserProfileRoutineListResponse.builder()
			.routines(routineResponses)
			.build();
	}

	private static UserProfileRoutineListResponse.UserProfileRoutineResponse toUserProfileRoutineRecordReadResponse(
		final Routine routine
	) {
		DaysOfWeekBitmask daysOfWeekBitmask = routine.getDaysOfWeekBitmask();
		List<DayOfWeek> daysOfWeek = daysOfWeekBitmask.getDaysOfWeek().stream()
			.sorted()
			.toList();

		return UserProfileRoutineListResponse.UserProfileRoutineResponse.builder()
			.routineId(routine.getId())
			.category(routine.getCategory())
			.color(routine.getColorValue())
			.title(routine.getTitle())
			.memo(routine.getMemoValue())
			.time(routine.getTime())
			.sharedCount(routine.getSharedCount())
			.daysOfWeek(daysOfWeek)
			.build();
	}
}
