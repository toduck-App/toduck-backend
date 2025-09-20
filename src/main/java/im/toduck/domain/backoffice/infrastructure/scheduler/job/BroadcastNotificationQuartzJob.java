package im.toduck.domain.backoffice.infrastructure.scheduler.job;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import im.toduck.domain.backoffice.domain.event.BroadcastNotificationExecutionEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConditionalOnProperty(name = "spring.quartz.auto-startup", havingValue = "true", matchIfMissing = true)
public class BroadcastNotificationQuartzJob extends QuartzJobBean {

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Override
	protected void executeInternal(final JobExecutionContext context) throws JobExecutionException {
		JobDataMap jobDataMap = context.getMergedJobDataMap();

		Long broadcastId = jobDataMap.getLong("broadcastId");
		String title = jobDataMap.getString("title");
		String message = jobDataMap.getString("message");

		log.info("브로드캐스트 알림 실행 이벤트 발행 시작 - BroadcastId: {}, Title: {}", broadcastId, title);

		try {
			BroadcastNotificationExecutionEvent event =
				new BroadcastNotificationExecutionEvent(broadcastId, title, message);
			eventPublisher.publishEvent(event);

			log.info("브로드캐스트 알림 실행 이벤트 발행 완료 - BroadcastId: {}", broadcastId);
		} catch (Exception e) {
			log.error("브로드캐스트 알림 실행 이벤트 발행 실패 - BroadcastId: {}", broadcastId, e);
			throw new JobExecutionException("브로드캐스트 알림 실행 이벤트 발행 실패", e);
		}
	}
}
