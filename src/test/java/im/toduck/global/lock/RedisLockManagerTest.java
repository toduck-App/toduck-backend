package im.toduck.global.lock;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import im.toduck.ServiceTest;

class RedisLockManagerTest extends ServiceTest {

	@Autowired
	private RedisLockManager redisLockManager;

	@Autowired
	private StringRedisTemplate redisTemplate;

	private static final String TEST_KEY = "test-key";
	private static final String TEST_VALUE = UUID.randomUUID().toString();
	private static final Duration TIMEOUT = Duration.ofSeconds(3);

	@BeforeEach
	void setUp() {
		redisTemplate.getConnectionFactory().getConnection().flushAll();
	}

	@Nested
	@DisplayName("락 획득시")
	class TryLockTest {

		@Test
		void 처음_시도하면_락_획득에_성공한다() {
			// when
			boolean result = redisLockManager.acquireLock(TEST_KEY, TEST_VALUE, TIMEOUT);

			// then
			assertSoftly(softly -> {
				softly.assertThat(result).isTrue();
				softly.assertThat(redisTemplate.opsForValue().get("lock:" + TEST_KEY))
					.isEqualTo(TEST_VALUE);
			});
		}

		@Test
		void 이미_락이_존재하면_획득에_실패한다() {
			// given
			redisLockManager.acquireLock(TEST_KEY, "other-value", TIMEOUT);

			// when
			boolean result = redisLockManager.acquireLock(TEST_KEY, TEST_VALUE, TIMEOUT);

			// then
			assertThat(result).isFalse();
		}

		@Test
		void 타임아웃_이후에는_다시_락을_획득할_수_있다() throws InterruptedException {
			// given
			Duration shortTimeout = Duration.ofMillis(100);
			redisLockManager.acquireLock(TEST_KEY, "other-value", shortTimeout);

			// when
			Thread.sleep(150);
			boolean result = redisLockManager.acquireLock(TEST_KEY, TEST_VALUE, TIMEOUT);

			// then
			assertThat(result).isTrue();
		}
	}

	@Nested
	@DisplayName("락 해제시")
	class UnlockTest {

		@Test
		void 자신이_획득한_락은_해제할_수_있다() {
			// given
			redisLockManager.acquireLock(TEST_KEY, TEST_VALUE, TIMEOUT);

			// when
			boolean result = redisLockManager.releaseLock(TEST_KEY, TEST_VALUE);

			// then
			assertSoftly(softly -> {
				softly.assertThat(result).isTrue();
				softly.assertThat(redisTemplate.opsForValue().get("lock:" + TEST_KEY))
					.isNull();
			});
		}

		@Test
		void 다른_값으로_설정된_락은_해제할_수_없다() {
			// given
			redisLockManager.acquireLock(TEST_KEY, "other-value", TIMEOUT);

			// when
			boolean result = redisLockManager.releaseLock(TEST_KEY, TEST_VALUE);

			// then
			assertSoftly(softly -> {
				softly.assertThat(result).isFalse();
				softly.assertThat(redisTemplate.opsForValue().get("lock:" + TEST_KEY))
					.isEqualTo("other-value");
			});
		}

		@Test
		void 존재하지_않는_락을_해제하려하면_실패한다() {
			// when
			boolean result = redisLockManager.releaseLock(TEST_KEY, TEST_VALUE);

			// then
			assertThat(result).isFalse();
		}
	}

	@Nested
	@DisplayName("동시성 처리시")
	class ConcurrencyTest {

		@Test
		void 여러_스레드가_동시에_락을_획득하려할_때_하나만_성공한다() throws InterruptedException {
			// given
			int threadCount = 10;
			ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
			CountDownLatch latch = new CountDownLatch(threadCount);
			AtomicInteger successCount = new AtomicInteger(0);

			// when
			for (int i = 0; i < threadCount; i++) {
				String value = "value-" + i;
				executorService.submit(() -> {
					try {
						if (redisLockManager.acquireLock(TEST_KEY, value, TIMEOUT)) {
							successCount.incrementAndGet();
						}
					} finally {
						latch.countDown();
					}
				});
			}

			latch.await(5, TimeUnit.SECONDS);
			executorService.shutdown();

			// then
			assertThat(successCount.get()).isEqualTo(1);
		}
	}
}
