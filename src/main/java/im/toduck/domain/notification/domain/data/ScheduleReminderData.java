package im.toduck.domain.notification.domain.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import im.toduck.domain.schedule.persistence.vo.ScheduleAlram;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 일정 알림에 사용하는 데이터
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleReminderData extends AbstractNotificationData {
	private Long scheduleId;
	private String scheduleTitle;
	private ScheduleAlram reminderType;
	@JsonProperty("allDay")
	private boolean isAllDay;

	public static ScheduleReminderData of(
			final Long scheduleId,
			final String scheduleTitle,
			final ScheduleAlram reminderType,
			final boolean isAllDay) {
		return new ScheduleReminderData(scheduleId, scheduleTitle, reminderType, isAllDay);
	}
}
