package im.toduck.domain.notification.domain.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import im.toduck.domain.notification.domain.service.NotificationService;
import im.toduck.domain.notification.domain.service.NotificationSettingService;
import im.toduck.domain.notification.domain.service.PushNotificationService;
import im.toduck.domain.notification.persistence.entity.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {
	private final NotificationService notificationService;
	private final PushNotificationService pushNotificationService;
	private final NotificationSettingService notificationSettingService;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Async
	@Transactional(propagation = Propagation.REQUIRES_NEW) // 항상 새로운 트랜잭션 시작
	public void handleNotificationEvent(final NotificationEvent<?> event) {
		log.info("알림 이벤트 처리 - 타입: {}, 사용자: {}", event.getType(), event.getUserId());

		Notification notification = notificationService.createNotification(event);
		log.info("알림 저장 완료 - ID: {}", notification.getId());

		if (notificationSettingService.isTypeEnabled(event.getUserId(), event.getType())) {
			pushNotificationService.sendPushNotification(notification);
			return;
		}

		log.info("푸시 알림 비활성화 - 사용자: {}, 타입: {}", event.getUserId(), event.getType());
	}
}
