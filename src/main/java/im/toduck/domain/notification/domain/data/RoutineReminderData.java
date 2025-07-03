package im.toduck.domain.notification.domain.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 루틴 알림에 사용하는 데이터
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoutineReminderData extends AbstractNotificationData {
	private Long routineId;
	private String routineTitle;
	private Integer reminderMinutes;
	private boolean isAllDay;

	public static RoutineReminderData of(
		final Long routineId,
		final String routineTitle,
		final Integer reminderMinutes,
		final boolean isAllDay
	) {
		return new RoutineReminderData(routineId, routineTitle, reminderMinutes, isAllDay);
	}
}
