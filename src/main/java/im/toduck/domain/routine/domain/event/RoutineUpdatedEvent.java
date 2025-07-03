package im.toduck.domain.routine.domain.event;

import im.toduck.domain.user.persistence.entity.User;
import lombok.Getter;

@Getter
public class RoutineUpdatedEvent {
	private final Long routineId;
	private final User user;
	private final boolean isTimeChanged;
	private final boolean isDaysOfWeekChanged;
	private final boolean isReminderMinutesChanged;
	private final boolean isTitleChanged;

	public RoutineUpdatedEvent(
		Long routineId,
		User user,
		boolean isTimeChanged,
		boolean isDaysOfWeekChanged,
		boolean isReminderMinutesChanged,
		boolean isTitleChanged
	) {
		this.routineId = routineId;
		this.user = user;
		this.isTimeChanged = isTimeChanged;
		this.isDaysOfWeekChanged = isDaysOfWeekChanged;
		this.isReminderMinutesChanged = isReminderMinutesChanged;
		this.isTitleChanged = isTitleChanged;
	}

	public boolean isReminderRelatedChanged() {
		return isTimeChanged || isDaysOfWeekChanged || isReminderMinutesChanged || isTitleChanged;
	}
}
