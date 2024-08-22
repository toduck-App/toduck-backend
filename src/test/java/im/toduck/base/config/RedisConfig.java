package im.toduck.base.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.testcontainers.containers.GenericContainer;

@Configuration
public class RedisConfig {

	private static final String REDIS_IMAGE = "redis:7.0.8-alpine";
	private static final int REDIS_PORT = 6379;  // 기본 포트로 변경
	private static final GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>(REDIS_IMAGE)
		.withExposedPorts(REDIS_PORT)
		.withReuse(true);

	static {
		REDIS_CONTAINER.start();
	}

	@Bean
	public LettuceConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(
			REDIS_CONTAINER.getHost(), REDIS_CONTAINER.getMappedPort(REDIS_PORT)
		);
		return new LettuceConnectionFactory(configuration);
	}
}
