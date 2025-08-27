package im.toduck.domain.notification.domain.event;

import im.toduck.domain.notification.domain.data.RoutineReminderData;
import im.toduck.domain.notification.persistence.entity.NotificationType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoutineReminderNotificationEvent extends NotificationEvent<RoutineReminderData> {

	private RoutineReminderNotificationEvent(Long userId, RoutineReminderData data) {
		super(userId, NotificationType.ROUTINE_REMINDER, data);
	}

	public static RoutineReminderNotificationEvent of(
		Long userId,
		Long routineId,
		String routineTitle,
		Integer reminderMinutes,
		boolean isAllDay
	) {
		return new RoutineReminderNotificationEvent(
			userId,
			RoutineReminderData.of(routineId, routineTitle, reminderMinutes, isAllDay)
		);
	}

	@Override
	public String getInAppTitle() {
		return "";
	}

	@Override
	public String getInAppBody() {
		return "";
	}

	@Override
	public String getPushTitle() {
		return getData().getRoutineTitle();
	}

	@Override
	public String getPushBody() {
		if (getData().isAllDay()) {
			return "루틴 하루 전! 나를 바꾸는 사소한 습관 💪🏻";
		}

		return String.format("루틴 %d분 전! 나를 바꾸는 사소한 습관 💪🏻", getData().getReminderMinutes());
	}

	@Override
	public String getActionUrl() {
		return "toduck://todo";
	}
}

