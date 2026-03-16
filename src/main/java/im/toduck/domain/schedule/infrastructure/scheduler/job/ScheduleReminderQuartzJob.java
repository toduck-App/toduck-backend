package im.toduck.domain.schedule.infrastructure.scheduler.job;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import im.toduck.domain.notification.domain.event.ScheduleReminderNotificationEvent;
import im.toduck.domain.notification.messaging.NotificationMessagePublisher;
import im.toduck.domain.schedule.persistence.vo.ScheduleAlram;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ScheduleReminderQuartzJob extends QuartzJobBean {

    @Autowired
    private NotificationMessagePublisher notificationMessagePublisher;

    @Override
    protected void executeInternal(final JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getMergedJobDataMap();

        Long userId = jobDataMap.getLong("userId");
        Long scheduleId = jobDataMap.getLong("scheduleId");
        String scheduleTitle = jobDataMap.getString("scheduleTitle");
        String reminderTypeName = jobDataMap.getString("reminderType");
        boolean isAllDay = jobDataMap.getBoolean("isAllDay");

        ScheduleAlram reminderType = ScheduleAlram.valueOf(reminderTypeName);

        log.info("일정 알림 발송 - UserId: {}, ScheduleId: {}, Title: {}", userId, scheduleId, scheduleTitle);

        try {
            ScheduleReminderNotificationEvent event = ScheduleReminderNotificationEvent.of(
                    userId, scheduleId, scheduleTitle, reminderType, isAllDay);

            notificationMessagePublisher.publishNotificationEvent(event);

            log.info("일정 알림 이벤트 발행 완료 - ScheduleId: {}", scheduleId);
        } catch (Exception e) {
            log.error("일정 알림 이벤트 발행 실패 - ScheduleId: {}", scheduleId, e);
            throw new JobExecutionException("일정 알림 이벤트 발행 실패", e);
        }
    }
}
