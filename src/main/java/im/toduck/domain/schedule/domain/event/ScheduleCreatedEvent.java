package im.toduck.domain.schedule.domain.event;

import lombok.Getter;

@Getter
public class ScheduleCreatedEvent {
    private final Long scheduleId;
    private final Long userId;

    public ScheduleCreatedEvent(final Long scheduleId, final Long userId) {
        this.scheduleId = scheduleId;
        this.userId = userId;
    }
}
