package im.toduck.domain.schedule.domain.event;

import lombok.Getter;

@Getter
public class ScheduleDeletedEvent {
    private final Long scheduleId;

    public ScheduleDeletedEvent(final Long scheduleId) {
        this.scheduleId = scheduleId;
    }
}
