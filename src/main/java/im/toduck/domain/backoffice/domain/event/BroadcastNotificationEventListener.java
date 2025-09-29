package im.toduck.domain.backoffice.domain.event;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.backoffice.domain.service.BroadcastNotificationService;
import im.toduck.domain.backoffice.persistence.entity.BroadcastNotification;
import im.toduck.domain.notification.domain.event.BroadcastNotificationEvent;
import im.toduck.domain.notification.messaging.NotificationMessagePublisher;
import im.toduck.domain.user.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "spring.quartz.auto-startup", havingValue = "true", matchIfMissing = true)
public class BroadcastNotificationEventListener {

	private final BroadcastNotificationService broadcastNotificationService;
	private final UserService userService;
	private final NotificationMessagePublisher notificationMessagePublisher;

	@EventListener
	@Async
	@Transactional
	public void handleBroadcastNotificationExecution(final BroadcastNotificationExecutionEvent event) {
		try {
			Optional<BroadcastNotification> optionalNotification =
				broadcastNotificationService.getBroadcastNotificationById(event.getBroadcastId());
			if (optionalNotification.isEmpty()) {
				log.error("브로드캐스트 알림을 찾을 수 없습니다 - BroadcastId: {}", event.getBroadcastId());
				return;
			}

			BroadcastNotification notification = optionalNotification.get();
			notification.markAsSending();
			broadcastNotificationService.save(notification);

			List<Long> activeUserIds = userService.getAllActiveUserIds();

			log.info("브로드캐스트 알림 발송 시작 - BroadcastId: {}, 대상 사용자 수: {}",
				event.getBroadcastId(), activeUserIds.size());

			// 각 사용자에게 브로드캐스트 알림 이벤트 발행
			for (Long userId : activeUserIds) {
				BroadcastNotificationEvent notificationEvent = BroadcastNotificationEvent.of(
					userId, notification.getTitle(), notification.getMessage()
				);
				notificationMessagePublisher.publishNotificationEvent(notificationEvent);
			}

			notification.markAsCompleted(activeUserIds.size());
			broadcastNotificationService.save(notification);

			log.info("브로드캐스트 알림 이벤트 발행 완료 - BroadcastId: {}, 발행된 이벤트 수: {}",
				event.getBroadcastId(), activeUserIds.size());

		} catch (Exception e) {
			Optional<BroadcastNotification> optionalNotification =
				broadcastNotificationService.getBroadcastNotificationById(event.getBroadcastId());
			if (optionalNotification.isPresent()) {
				BroadcastNotification notification = optionalNotification.get();
				notification.markAsFailed(e.getMessage());
				broadcastNotificationService.save(notification);
			}
			log.error("브로드캐스트 알림 실행 실패 - BroadcastId: {}", event.getBroadcastId(), e);
		}
	}
}
