package im.toduck.domain.notification.domain.event;

import im.toduck.domain.notification.domain.data.ScheduleReminderData;
import im.toduck.domain.notification.persistence.entity.NotificationType;
import im.toduck.domain.schedule.persistence.vo.ScheduleAlram;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ScheduleReminderNotificationEvent extends NotificationEvent<ScheduleReminderData> {

    private ScheduleReminderNotificationEvent(final Long userId, final ScheduleReminderData data) {
        super(userId, NotificationType.SCHEDULE_REMINDER, data);
    }

    public static ScheduleReminderNotificationEvent of(
            final Long userId,
            final Long scheduleId,
            final String scheduleTitle,
            final ScheduleAlram reminderType,
            final boolean isAllDay) {
        return new ScheduleReminderNotificationEvent(
                userId,
                ScheduleReminderData.of(scheduleId, scheduleTitle, reminderType, isAllDay));
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
        return getData().getScheduleTitle();
    }

    @Override
    public String getPushBody() {
        if (getData().isAllDay()) {
            return "일정 하루 전! 준비된 하루를 시작해볼까요? 📅";
        }

        ScheduleAlram reminderType = getData().getReminderType();
        return String.format("일정 %d분 전! 준비된 하루를 시작해볼까요? 📅", reminderType.getMinutes());
    }

    @Override
    public String getActionUrl() {
        return "toduck://todo";
    }
}
