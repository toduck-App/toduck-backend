package im.toduck.domain.schedule.domain.event;

import lombok.Getter;

@Getter
public class ScheduleUpdatedEvent {
    private final Long scheduleId;
    private final Long userId;
    private final boolean isAlarmChanged;
    private final boolean isTimeChanged;
    private final boolean isAllDayChanged;
    private final boolean isTitleChanged;
    private final boolean isDateChanged;
    private final boolean isDaysOfWeekChanged;

    public ScheduleUpdatedEvent(
            final Long scheduleId,
            final Long userId,
            final boolean isAlarmChanged,
            final boolean isTimeChanged,
            final boolean isAllDayChanged,
            final boolean isTitleChanged,
            final boolean isDateChanged,
            final boolean isDaysOfWeekChanged) {
        this.scheduleId = scheduleId;
        this.userId = userId;
        this.isAlarmChanged = isAlarmChanged;
        this.isTimeChanged = isTimeChanged;
        this.isAllDayChanged = isAllDayChanged;
        this.isTitleChanged = isTitleChanged;
        this.isDateChanged = isDateChanged;
        this.isDaysOfWeekChanged = isDaysOfWeekChanged;
    }

    public boolean isReminderRelatedChanged() {
        return isAlarmChanged || isTimeChanged || isAllDayChanged || isTitleChanged
                || isDateChanged || isDaysOfWeekChanged;
    }
}
