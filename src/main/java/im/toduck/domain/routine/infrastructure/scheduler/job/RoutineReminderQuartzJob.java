package im.toduck.domain.routine.infrastructure.scheduler.job;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import im.toduck.domain.notification.domain.event.RoutineReminderNotificationEvent;
import im.toduck.domain.notification.messaging.NotificationMessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoutineReminderQuartzJob extends QuartzJobBean {

	private final NotificationMessagePublisher notificationMessagePublisher;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		JobDataMap jobDataMap = context.getMergedJobDataMap();

		Long userId = jobDataMap.getLong("userId");
		Long routineId = jobDataMap.getLong("routineId");
		String routineTitle = jobDataMap.getString("routineTitle");
		Integer reminderMinutes = jobDataMap.getInt("reminderMinutes");
		boolean isAllDay = jobDataMap.getBoolean("isAllDay");

		log.info("루틴 알림 발송 - UserId: {}, RoutineId: {}, Title: {}", userId, routineId, routineTitle);

		try {
			RoutineReminderNotificationEvent event = RoutineReminderNotificationEvent.of(
				userId, routineId, routineTitle, reminderMinutes, isAllDay
			);

			notificationMessagePublisher.publishNotificationEvent(event);

			log.info("루틴 알림 이벤트 발행 완료 - RoutineId: {}", routineId);
		} catch (Exception e) {
			log.error("루틴 알림 이벤트 발행 실패 - RoutineId: {}", routineId, e);
			throw new JobExecutionException("루틴 알림 이벤트 발행 실패", e);
		}
	}
}
