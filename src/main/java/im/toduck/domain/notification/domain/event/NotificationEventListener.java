package im.toduck.domain.notification.domain.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import im.toduck.domain.notification.messaging.NotificationMessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {
	private final NotificationMessagePublisher messagePublisher;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleNotificationEvent(final NotificationEvent<?> event) {
		log.info("알림 이벤트 감지 - 타입: {}, 사용자: {}", event.getType(), event.getUserId());
		messagePublisher.publishNotificationEvent(event);
	}
}
