package im.toduck.domain.routine.common.mapper;

import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.vo.PlanCategoryColor;
import im.toduck.domain.routine.persistence.vo.RoutineMemo;
import im.toduck.domain.routine.presentation.dto.request.RoutineCreateRequest;
import im.toduck.domain.routine.presentation.dto.response.RoutineCreateResponse;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.helper.DaysOfWeekBitmask;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class RoutineMapper {
	public static Routine toRoutine(User user, RoutineCreateRequest request) {
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

	public static RoutineCreateResponse toRoutineCreateResponse(Routine routine) {
		return RoutineCreateResponse.builder()
			.routineId(routine.getId())
			.build();
	}

}
