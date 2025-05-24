package im.toduck.domain.notification.messaging;

import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import im.toduck.domain.notification.domain.event.NotificationEvent;
import im.toduck.global.config.rabbitmq.RabbitMqConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationMessagePublisher {

	private final RabbitTemplate rabbitTemplate;

	public void publishNotificationEvent(NotificationEvent<?> event) {
		int priority = determineNotificationPriority(event);

		log.info("알림 이벤트 발행 - 타입: {}, 사용자: {}, 우선순위: {}", event.getType(), event.getUserId(), priority);

		rabbitTemplate.convertAndSend(
			RabbitMqConfig.NOTIFICATION_EXCHANGE,
			RabbitMqConfig.NOTIFICATION_ROUTING_KEY,
			event,
			message -> {
				MessageProperties props = message.getMessageProperties();
				props.setPriority(priority);
				return message;
			}
		);
	}

	private int determineNotificationPriority(NotificationEvent<?> event) {
		return event.getType().getPriority().getLevel();
	}
}
