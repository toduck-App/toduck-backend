package im.toduck.global.config.shedlock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;

/**
 * ShedLock 설정 클래스
 * <p>
 * Redis를 사용하여 분산 환경에서 스케줄러의 중복 실행을 방지합니다.
 * <p>
 */
@Configuration
@EnableSchedulerLock(defaultLockAtMostFor = "10m")
public class ShedLockConfig {

	/**
	 * Redis 기반 락 프로바이더를 생성합니다.
	 *
	 * @param connectionFactory Redis 연결 팩토리
	 * @return Redis 락 프로바이더
	 */
	@Bean
	public LockProvider lockProvider(RedisConnectionFactory connectionFactory) {
		return new RedisLockProvider(connectionFactory, "toduck");
	}
}
