package im.toduck.domain.routine.domain.event;

import lombok.Getter;

@Getter
public class RoutineDeletedEvent {
	private final Long routineId;

	public RoutineDeletedEvent(Long routineId) {
		this.routineId = routineId;
	}
}
