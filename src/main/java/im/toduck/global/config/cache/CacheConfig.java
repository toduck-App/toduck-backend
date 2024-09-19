package im.toduck.global.config.cache;

import java.time.Duration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@EnableCaching
@Configuration
public class CacheConfig {
	private static final long OIDC_CACHE_TTL_DAY = 3;

	@Bean
	public CacheManager oidcCacheManager(RedisConnectionFactory cf) {
		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
			.serializeKeysWith(
				RedisSerializationContext.SerializationPair.fromSerializer(
					new StringRedisSerializer()
				))
			.serializeValuesWith(
				RedisSerializationContext.SerializationPair.fromSerializer(
					new GenericJackson2JsonRedisSerializer()
				))
			.entryTtl(Duration.ofDays(OIDC_CACHE_TTL_DAY));

		return RedisCacheManager
			.RedisCacheManagerBuilder
			.fromConnectionFactory(cf)
			.cacheDefaults(config)
			.build();
	}
}
