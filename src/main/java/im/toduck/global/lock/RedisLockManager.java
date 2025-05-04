package im.toduck.global.lock;

import java.time.Duration;
import java.util.Collections;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisLockManager {
	private final StringRedisTemplate redisTemplate;

	private static final String LOCK_PREFIX = "lock:";
	private static final String UNLOCK_SCRIPT =
		"if redis.call('get', KEYS[1]) == ARGV[1] then "
			+ "   return redis.call('del', KEYS[1]) "
			+ "else "
			+ "   return 0 "
			+ "end";

	private static final DefaultRedisScript<Long> UNLOCK_REDIS_SCRIPT =
		new DefaultRedisScript<>(UNLOCK_SCRIPT, Long.class);

	public boolean tryLock(String key, String value, Duration timeout) {
		try {
			String lockKey = LOCK_PREFIX + key;
			return Boolean.TRUE.equals(
				redisTemplate.opsForValue().setIfAbsent(lockKey, value, timeout)
			);
		} catch (Exception e) {
			log.error("[RedisLockManager] 락 획득 중 오류 발생 - 키: {}", key, e);
			return false;
		}
	}

	public boolean unlock(String key, String value) {
		try {
			String lockKey = LOCK_PREFIX + key;
			Long result = redisTemplate.execute(
				UNLOCK_REDIS_SCRIPT,
				Collections.singletonList(lockKey),
				value
			);
			return Long.valueOf(1).equals(result);
		} catch (Exception e) {
			log.error("[RedisLockManager] 락 해제 중 오류 발생 - 키: {}", key, e);
			return false;
		}
	}
}
