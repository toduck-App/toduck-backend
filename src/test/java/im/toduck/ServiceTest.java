package im.toduck;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.google.firebase.messaging.FirebaseMessaging;

import im.toduck.builder.BuilderSupporter;
import im.toduck.builder.TestFixtureBuilder;
import im.toduck.domain.notification.messaging.NotificationMessagePublisher;
import im.toduck.global.security.jwt.access.AccessTokenProvider;
import im.toduck.global.security.jwt.refresh.RefreshTokenProvider;
import im.toduck.infra.push.FirebaseConfig;
import im.toduck.infra.redis.forbidden.ForbiddenTokenService;
import im.toduck.infra.redis.refresh.RefreshTokenService;
import im.toduck.infra.sms.VerifiyCodeUtil;
import jakarta.persistence.EntityManager;

@SpringBootTest
@ActiveProfiles("test")
@Import(value = {TestFixtureBuilder.class, BuilderSupporter.class})
public abstract class ServiceTest {
	@Autowired
	protected TestFixtureBuilder testFixtureBuilder;

	@MockBean
	protected AccessTokenProvider accessTokenProvider;

	@MockBean
	protected RefreshTokenProvider refreshTokenProvider;

	@MockBean
	protected RefreshTokenService refreshTokenService;

	@MockBean
	protected ForbiddenTokenService forbiddenTokenService;

	@MockBean
	protected VerifiyCodeUtil verifiyCodeUtil;

	@MockBean
	private FirebaseMessaging firebaseMessaging;

	@MockBean
	private FirebaseConfig firebaseConfig;

	@MockBean
	private RabbitTemplate rabbitTemplate;

	@MockBean
	private NotificationMessagePublisher notificationMessagePublisher;

	@Autowired
	protected EntityManager entityManager;
}
