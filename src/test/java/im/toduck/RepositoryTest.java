package im.toduck;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.redis.AutoConfigureDataRedis;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTypeExcludeFilter;
import org.springframework.boot.test.autoconfigure.filter.TypeExcludeFilters;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.google.firebase.messaging.FirebaseMessaging;

import im.toduck.builder.BuilderSupporter;
import im.toduck.builder.TestFixtureBuilder;
import im.toduck.domain.notification.messaging.NotificationMessagePublisher;
import im.toduck.global.config.querydsl.QueryDslConfig;
import im.toduck.infra.push.FirebaseConfig;

/**
 * 이 추상 클래스는 JPA와 Redis 기능을 모두 필요로 하는 리포지토리 테스트를 위한 기본 구성을 제공합니다.
 * {@link org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest}와
 * {@link org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest}의 기능을 결합하여
 * JPA 리포지토리와 Redis 작업의 통합 테스팅을 가능하게 합니다.
 *
 * <p>주요 특징:</p>
 * <ul>
 *   <li>JPA와 Redis 테스트 환경을 모두 구성합니다.</li>
 *   <li>"test" 프로필을 활성화합니다.</li>
 *   <li> FeignClient Bean 생성을 위한 Config를 import 합니다.</li>
 *   <li>테스트 픽스처를 위한 필요한 설정과 빌더를 가져옵니다.</li>
 *   <li>테스트를 위한 트랜잭션 관리를 활성화합니다.</li>
 * </ul>
 *
 * <p>사용법: JPA와 Redis 테스팅 기능을 모두 활용하려면 리포지토리 테스트 클래스에서 이 클래스를 상속하세요.</p>
 *
 * @see org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
 * @see org.springframework.boot.test.autoconfigure.data.redis.AutoConfigureDataRedis
 */
@DataJpaTest
@TypeExcludeFilters(DataRedisTypeExcludeFilter.class)
@ImportAutoConfiguration({FeignAutoConfiguration.class})
@AutoConfigureDataRedis
@ActiveProfiles("test")
@TestPropertySource(properties = {
	"spring.quartz.auto-startup=false"
})
@Import(value = {TestFixtureBuilder.class, BuilderSupporter.class, QueryDslConfig.class})
public abstract class RepositoryTest {

	@Autowired
	protected TestFixtureBuilder testFixtureBuilder;

	@MockBean
	private FirebaseMessaging firebaseMessaging;

	@MockBean
	private FirebaseConfig firebaseConfig;

	@MockBean
	private RabbitTemplate rabbitTemplate;

	@MockBean
	private NotificationMessagePublisher notificationMessagePublisher;
}
