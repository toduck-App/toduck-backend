package im.toduck.domain.routine.domain.event;

import im.toduck.domain.user.persistence.entity.User;
import lombok.Getter;

@Getter
public class RoutineCreatedEvent {
	private final Long routineId;
	private final User user;

	public RoutineCreatedEvent(Long routineId, User user) {
		this.routineId = routineId;
		this.user = user;
	}
}
