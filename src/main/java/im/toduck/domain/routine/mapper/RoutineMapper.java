package im.toduck.domain.routine.mapper;

import java.time.DayOfWeek;
import java.util.List;

import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.presentation.dto.request.RoutineCreateRequest;
import im.toduck.domain.routine.presentation.dto.response.RoutineCreateResponse;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.annotation.Mapper;
import im.toduck.global.helper.DaysOfWeekBitmask;

@Mapper
public class RoutineMapper {
	public Routine toRoutine(User user, RoutineCreateRequest request) {
		DaysOfWeekBitmask daysOfWeekBitmask = getDaysOfWeekBitmask(request.daysOfWeek());

		return Routine.builder()
			.user(user)
			.category(request.category())
			.color(request.color())
			.title(request.title())
			.memo(request.memo())
			.isPublic(request.isPublic())
			.reminderMinutes(request.reminderMinutes())
			.time(request.time())
			.daysOfWeekBitmask(daysOfWeekBitmask)
			.build();
	}

	public RoutineCreateResponse toRoutineCreateResponse(Routine routine) {
		return RoutineCreateResponse.builder()
			.routineId(routine.getId())
			.build();
	}

	private DaysOfWeekBitmask getDaysOfWeekBitmask(List<DayOfWeek> daysOfWeek) {
		return DaysOfWeekBitmask.createByDayOfWeek(daysOfWeek);
	}
}
