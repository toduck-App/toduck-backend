package im.toduck.global.lock;

import java.time.Duration;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DistributedLock {

	private final RedisLockManager lockManager;

	public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(3);
	public static final int DEFAULT_MAX_RETRIES = 5;
	public static final Duration DEFAULT_RETRY_DELAY = Duration.ofMillis(100);

	/**
	 * 분산 락을 획득하고 작업을 실행합니다.
	 *
	 * @param key 락 식별자
	 * @param task 실행할 작업
	 * @return 작업 실행 결과
	 * @throws LockAcquisitionException 락 획득 실패 시
	 * @see #executeWithLock(String, Duration, int, Duration, Supplier)
	 */
	public <T> T executeWithLock(String key, Supplier<T> task) {
		return executeWithLock(key, DEFAULT_TIMEOUT, DEFAULT_MAX_RETRIES, DEFAULT_RETRY_DELAY, task);
	}

	/**
	 * @see #executeWithLock(String, Duration, int, Duration, Supplier)
	 */
	public <T> T executeWithLock(String key, Duration timeout, Supplier<T> task) {
		return executeWithLock(key, timeout, DEFAULT_MAX_RETRIES, DEFAULT_RETRY_DELAY, task);
	}

	/**
	 * @see #executeWithLock(String, Duration, int, Duration, Supplier)
	 */
	public <T> T executeWithLock(String key, Duration timeout, int maxRetries, Supplier<T> task) {
		return executeWithLock(key, timeout, maxRetries, DEFAULT_RETRY_DELAY, task);
	}

	/**
	 * 분산 락을 획득하고 작업을 실행합니다.
	 *
	 * @param key 락 식별자
	 * @param timeout 락 타임아웃
	 * @param maxRetries 최대 재시도 횟수
	 * @param retryDelay 재시도 대기 시간
	 * @param task 실행할 작업
	 * @return 작업 실행 결과
	 * @throws IllegalArgumentException 키가 null이거나 빈 문자열인 경우
	 * @throws LockAcquisitionException 락 획득 실패 시
	 */
	public <T> T executeWithLock(String key, Duration timeout, int maxRetries, Duration retryDelay, Supplier<T> task) {
		if (key == null || key.isBlank()) {
			throw new IllegalArgumentException("락 키는 필수값입니다.");
		}

		String lockValue = UUID.randomUUID().toString();

		for (int attempt = 1; attempt <= maxRetries; attempt++) {
			if (acquireLock(key, lockValue, timeout)) {
				try {
					log.debug("[DistributedLock] 락 획득 성공 - 키: {}, 시도: {}", key, attempt);
					return task.get();
				} finally {
					releaseLock(key, lockValue);
				}
			}

			if (attempt < maxRetries) {
				log.debug("[DistributedLock] 락 획득 실패, 재시도 - 키: {}, 시도: {}/{}", key, attempt, maxRetries);
				waitBeforeRetry(retryDelay);
			}
		}

		log.warn("[DistributedLock] 최대 재시도 횟수 초과 - 키: {}, 최대시도: {}", key, maxRetries);
		throw new LockAcquisitionException(key, maxRetries);
	}

	/**
	 * 반환값이 없는 작업을 위한 메서드
	 *
	 * @param key 락 식별자
	 * @param task 실행할 작업
	 * @throws LockAcquisitionException 락 획득 실패 시
	 * @see #executeWithLock(String, Duration, int, Duration, Supplier)
	 */
	public void executeWithLock(String key, Runnable task) {
		executeWithLock(key, DEFAULT_TIMEOUT, DEFAULT_MAX_RETRIES, DEFAULT_RETRY_DELAY, () -> {
			task.run();
			return null;
		});
	}

	/**
	 * @see #executeWithLock(String, Duration, int, Duration, Supplier)
	 */
	public void executeWithLock(String key, Duration timeout, Runnable task) {
		executeWithLock(key, timeout, DEFAULT_MAX_RETRIES, DEFAULT_RETRY_DELAY, () -> {
			task.run();
			return null;
		});
	}

	/**
	 * @see #executeWithLock(String, Duration, int, Duration, Supplier)
	 */
	public void executeWithLock(String key, Duration timeout, int maxRetries, Runnable task) {
		executeWithLock(key, timeout, maxRetries, DEFAULT_RETRY_DELAY, () -> {
			task.run();
			return null;
		});
	}

	/**
	 * @see #executeWithLock(String, Duration, int, Duration, Supplier)
	 */
	public void executeWithLock(String key, Duration timeout, int maxRetries, Duration retryDelay, Runnable task) {
		executeWithLock(key, timeout, maxRetries, retryDelay, () -> {
			task.run();
			return null;
		});
	}

	private boolean acquireLock(String key, String lockValue, Duration timeout) {
		return lockManager.tryLock(key, lockValue, timeout);
	}

	private void releaseLock(String key, String lockValue) {
		try {
			boolean released = lockManager.unlock(key, lockValue);
			if (released) {
				log.debug("[DistributedLock] 락 해제 성공 - 키: {}", key);
			} else {
				log.warn("[DistributedLock] 락 해제 실패 - 키: {}", key);
			}
		} catch (Exception e) {
			log.error("[DistributedLock] 락 해제 중 오류 발생 - 키: {}", key, e);
		}
	}

	private void waitBeforeRetry(Duration retryDelay) {
		try {
			Thread.sleep(retryDelay.toMillis());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("락 획득 대기 중 인터럽트 발생", e);
		}
	}
}
