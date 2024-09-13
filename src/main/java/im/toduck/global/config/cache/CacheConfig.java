package im.toduck.global.config.cache;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class CacheConfig {
	private final String host;
	private final int port;
	private final String password;

	private final long oidcCacheTtlDay = 3;

	public CacheConfig(
		@Value("${spring.data.redis.host}") String host,
		@Value("${spring.data.redis.port}") int port,
		@Value("${spring.data.redis.password}") String password
	) {
		this.host = host;
		this.port = port;
		this.password = password;
	}

	@Bean
	@Primary
	@Qualifier("redisCacheConnectionFactory")
	public RedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
		config.setPassword(password);
		LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder().build();
		return new LettuceConnectionFactory(config, clientConfig);
	}

	@Bean
	public CacheManager oidcCacheManager(@Qualifier("redisCacheConnectionFactory") RedisConnectionFactory cf) {
		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
			.serializeKeysWith(
				RedisSerializationContext.SerializationPair.fromSerializer(
					new StringRedisSerializer()
				))
			.serializeValuesWith(
				RedisSerializationContext.SerializationPair.fromSerializer(
					new GenericJackson2JsonRedisSerializer()
				))
			.entryTtl(Duration.ofDays(oidcCacheTtlDay));

		return RedisCacheManager
			.RedisCacheManagerBuilder
			.fromConnectionFactory(cf)
			.cacheDefaults(config)
			.build();
	}
}
