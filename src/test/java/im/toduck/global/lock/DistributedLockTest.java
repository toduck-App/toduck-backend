package im.toduck.global.lock;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import im.toduck.ServiceTest;

class DistributedLockTest extends ServiceTest {

	@Autowired
	private DistributedLock distributedLock;

	@MockBean
	private RedisLockManager redisLockManager;

	@Nested
	@DisplayName("분산 락 작업 실행시")
	class ExecuteWithLockTest {

		@Test
		void 락_획득에_성공하면_작업이_실행된다() {
			// given
			String lockKey = "test-lock";
			String expectedResult = "success";

			given(redisLockManager.tryLock(eq(lockKey), anyString(), any(Duration.class)))
				.willReturn(true);
			given(redisLockManager.unlock(eq(lockKey), anyString()))
				.willReturn(true);

			// when
			String result = distributedLock.executeWithLock(lockKey, () -> expectedResult);

			// then
			assertSoftly(softly -> {
				softly.assertThat(result).isEqualTo(expectedResult);
				verify(redisLockManager).tryLock(eq(lockKey), anyString(), any(Duration.class));
				verify(redisLockManager).unlock(eq(lockKey), anyString());
			});
		}

		@Test
		void 작업_실행중_예외가_발생하면_락이_해제된다() {
			// given
			String lockKey = "test-lock";
			RuntimeException expectedException = new RuntimeException("작업 실패");

			given(redisLockManager.tryLock(eq(lockKey), anyString(), any(Duration.class)))
				.willReturn(true);
			given(redisLockManager.unlock(eq(lockKey), anyString()))
				.willReturn(true);

			// when & then
			assertThatThrownBy(() ->
				distributedLock.executeWithLock(lockKey, () -> {
					throw expectedException;
				})
			)
				.isInstanceOf(RuntimeException.class)
				.hasMessage("작업 실패");

			verify(redisLockManager).unlock(eq(lockKey), anyString());
		}

		@Test
		void 락_획득에_실패하면_재시도한다() {
			// given
			String lockKey = "test-lock";
			String expectedResult = "success";

			given(redisLockManager.tryLock(eq(lockKey), anyString(), any(Duration.class)))
				.willReturn(false)  // 첫 번째 시도 실패
				.willReturn(true);  // 두 번째 시도 성공
			given(redisLockManager.unlock(eq(lockKey), anyString()))
				.willReturn(true);

			// when
			String result = distributedLock.executeWithLock(lockKey, () -> expectedResult);

			// then
			assertSoftly(softly -> {
				softly.assertThat(result).isEqualTo(expectedResult);
				verify(redisLockManager, times(2)).tryLock(eq(lockKey), anyString(), any(Duration.class));
				verify(redisLockManager).unlock(eq(lockKey), anyString());
			});
		}

		@Test
		void 최대_재시도_횟수를_초과하면_예외가_발생한다() {
			// given
			String lockKey = "test-lock";
			int maxRetries = 3;

			given(redisLockManager.tryLock(eq(lockKey), anyString(), any(Duration.class)))
				.willReturn(false);

			// when & then
			assertThatThrownBy(() ->
				distributedLock.executeWithLock(lockKey, Duration.ofSeconds(1), maxRetries, () -> "result")
			)
				.isInstanceOf(LockAcquisitionException.class);

			verify(redisLockManager, times(maxRetries)).tryLock(eq(lockKey), anyString(), any(Duration.class));
		}

		@Test
		void null_또는_빈_키를_전달하면_예외가_발생한다() {
			// when & then
			assertSoftly(softly -> {
				softly.assertThatThrownBy(() ->
						distributedLock.executeWithLock(null, () -> "result")
					)
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessage("락 키는 필수값입니다.");

				softly.assertThatThrownBy(() ->
						distributedLock.executeWithLock("", () -> "result")
					)
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessage("락 키는 필수값입니다.");

				softly.assertThatThrownBy(() ->
						distributedLock.executeWithLock("   ", () -> "result")
					)
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessage("락 키는 필수값입니다.");
			});
		}
	}

	@Nested
	@DisplayName("void 반환 작업 실행시")
	class ExecuteVoidTaskTest {

		@Test
		void Runnable_작업이_정상적으로_실행된다() {
			// given
			String lockKey = "test-lock";
			AtomicInteger counter = new AtomicInteger(0);

			given(redisLockManager.tryLock(eq(lockKey), anyString(), any(Duration.class)))
				.willReturn(true);
			given(redisLockManager.unlock(eq(lockKey), anyString()))
				.willReturn(true);

			// when
			distributedLock.executeWithLock(lockKey, counter::incrementAndGet);

			// then
			assertSoftly(softly -> {
				softly.assertThat(counter.get()).isEqualTo(1);
				verify(redisLockManager).tryLock(eq(lockKey), anyString(), any(Duration.class));
				verify(redisLockManager).unlock(eq(lockKey), anyString());
			});
		}

		@Test
		void 타임아웃을_지정한_Runnable_작업이_정상적으로_실행된다() {
			// given
			String lockKey = "test-lock";
			Duration timeout = Duration.ofSeconds(5);
			AtomicInteger counter = new AtomicInteger(0);

			given(redisLockManager.tryLock(eq(lockKey), anyString(), eq(timeout)))
				.willReturn(true);
			given(redisLockManager.unlock(eq(lockKey), anyString()))
				.willReturn(true);

			// when
			distributedLock.executeWithLock(lockKey, timeout, counter::incrementAndGet);

			// then
			assertSoftly(softly -> {
				softly.assertThat(counter.get()).isEqualTo(1);
				verify(redisLockManager).tryLock(eq(lockKey), anyString(), eq(timeout));
				verify(redisLockManager).unlock(eq(lockKey), anyString());
			});
		}
	}
}
