package im.toduck.global.config.rabbitmq;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import im.toduck.domain.notification.common.serializer.NotificationMapperFactory;

@Configuration
public class RabbitMqConfig {
	public static final String NOTIFICATION_QUEUE = "notification.queue";
	public static final String NOTIFICATION_DLQ = "notification.dlq";

	public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
	public static final String NOTIFICATION_DLX = "notification.dlx";

	public static final String NOTIFICATION_ROUTING_KEY = "notification.key";

	@Bean
	public ObjectMapper rabbitObjectMapper() {
		return NotificationMapperFactory.createObjectMapper();
	}

	@Bean
	public Jackson2JsonMessageConverter messageConverter(final ObjectMapper rabbitObjectMapper) {
		return new Jackson2JsonMessageConverter(rabbitObjectMapper);
	}

	@Bean
	public RabbitTemplate rabbitTemplate(
		final ConnectionFactory connectionFactory,
		final Jackson2JsonMessageConverter messageConverter
	) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setMessageConverter(messageConverter);
		return template;
	}

	@Bean
	public Queue notificationQueue() {
		Map<String, Object> args = new HashMap<>();
		args.put("x-dead-letter-exchange", NOTIFICATION_DLX);
		args.put("x-dead-letter-routing-key", NOTIFICATION_ROUTING_KEY);
		args.put("x-max-priority", 10);
		return new Queue(NOTIFICATION_QUEUE, true, false, false, args);
	}

	@Bean
	public Queue notificationDlq() {
		return new Queue(NOTIFICATION_DLQ, true);
	}

	@Bean
	public DirectExchange notificationExchange() {
		return new DirectExchange(NOTIFICATION_EXCHANGE);
	}

	@Bean
	public DirectExchange notificationDlx() {
		return new DirectExchange(NOTIFICATION_DLX);
	}

	@Bean
	public Binding notificationBinding() {
		return BindingBuilder.bind(notificationQueue())
			.to(notificationExchange())
			.with(NOTIFICATION_ROUTING_KEY);
	}

	@Bean
	public Binding dlqBinding() {
		return BindingBuilder.bind(notificationDlq())
			.to(notificationDlx())
			.with(NOTIFICATION_ROUTING_KEY);
	}
}
