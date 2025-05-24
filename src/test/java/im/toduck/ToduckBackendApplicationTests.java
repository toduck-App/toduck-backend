package im.toduck;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.google.firebase.messaging.FirebaseMessaging;

import im.toduck.domain.notification.messaging.NotificationMessagePublisher;
import im.toduck.infra.push.FirebaseConfig;

@SpringBootTest
@ActiveProfiles("test")
class ToduckBackendApplicationTests {

	@MockBean
	private FirebaseMessaging firebaseMessaging;

	@MockBean
	private FirebaseConfig firebaseConfig;

	@MockBean
	private RabbitTemplate rabbitTemplate;

	@MockBean
	private NotificationMessagePublisher notificationMessagePublisher;

	@Test
	void contextLoads() {
	}
}
