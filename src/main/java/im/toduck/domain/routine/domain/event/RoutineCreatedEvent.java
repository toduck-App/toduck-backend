package im.toduck.domain.routine.domain.event;

import lombok.Getter;

@Getter
public class RoutineCreatedEvent {
	private final Long routineId;
	private final Long userId;

	public RoutineCreatedEvent(Long routineId, Long userId) {
		this.routineId = routineId;
		this.userId = userId;
	}
}
