package im.toduck.domain.notification.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.notification.domain.event.NotificationEvent;
import im.toduck.domain.notification.domain.service.NotificationService;
import im.toduck.domain.notification.domain.service.NotificationSettingService;
import im.toduck.domain.notification.domain.service.PushNotificationService;
import im.toduck.domain.notification.persistence.entity.Notification;
import im.toduck.global.config.rabbitmq.RabbitMqConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationMessageConsumer {

	private final NotificationService notificationService;
	private final NotificationSettingService notificationSettingService;
	private final PushNotificationService pushNotificationService;

	@RabbitListener(queues = RabbitMqConfig.NOTIFICATION_QUEUE)
	@Transactional
	public void processNotification(NotificationEvent<?> event) {
		log.info("알림 처리 시작 - 타입: {}, 사용자: {}", event.getType(), event.getUserId());
		processNotificationEvent(event);
	}

	private void processNotificationEvent(NotificationEvent<?> event) {
		try {
			Notification notification = notificationService.createNotification(event);
			log.info("알림 저장 완료 - ID: {}", notification.getId());

			if (notificationSettingService.isTypeEnabled(event.getUserId(), event.getType())) {
				pushNotificationService.sendPushNotification(notification);
				return;
			}

			log.info("푸시 알림 비활성화 - 사용자: {}, 타입: {}", event.getUserId(), event.getType());
		} catch (Exception e) {
			log.error("알림 처리 중 오류 발생 - 타입: {}, 사용자: {}", event.getType(), event.getUserId(), e);
			throw e;
		}
	}

	@RabbitListener(queues = RabbitMqConfig.NOTIFICATION_DLQ)
	public void processDlq(NotificationEvent<?> event) {
		log.error("알림 처리 최종 실패 (DLQ) - 타입: {}, 사용자: {}", event.getType(), event.getUserId());
		// 추가적인 복구 로직, 추후 모니터링 시 사용
	}
}
