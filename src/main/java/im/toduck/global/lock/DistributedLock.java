package im.toduck.global.lock;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DistributedLock {

	private final RedisLockManager lockManager;
	private final TransactionExecutor transactionExecutor;

	public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(3);
	public static final int DEFAULT_MAX_RETRIES = 5;
	public static final Duration DEFAULT_RETRY_DELAY = Duration.ofMillis(100);

	/**
	 * Acquires a distributed lock identified by the given key and executes the provided task within a new, independent transaction.
	 *
	 * The lock is attempted with the specified timeout, retry count, and retry delay. If the lock cannot be acquired after all retries, a {@code LockAcquisitionException} is thrown. The task is executed in a new transaction, ensuring isolation from any existing transactions. The lock is always released after task execution.
	 *
	 * @param key the unique identifier for the lock; must not be null or blank
	 * @param timeout the maximum duration to hold the lock before it expires
	 * @param maxRetries the maximum number of attempts to acquire the lock
	 * @param retryDelay the delay between retry attempts
	 * @param task the operation to execute once the lock is acquired
	 * @return the result of the executed task
	 * @throws IllegalArgumentException if the key is null or blank
	 * @throws LockAcquisitionException if the lock cannot be acquired after all retries
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
					return transactionExecutor.executeInNewTransaction(task);
				} finally {
					releaseLock(key, lockValue);
				}
			}

			if (attempt < maxRetries) {
				log.debug("[DistributedLock] 락 획득 실패, 재시도 - 키: {}, 시도: {}/{}", key, attempt, maxRetries);
				waitBeforeRetry(retryDelay, attempt);
			}
		}

		log.warn("[DistributedLock] 최대 재시도 횟수 초과 - 키: {}, 최대시도: {}", key, maxRetries);
		throw new LockAcquisitionException(key, maxRetries);
	}

	/**
	 * Acquires a distributed lock for the given key and executes the provided task within a new transaction.
	 *
	 * <p>
	 * Uses default timeout, maximum retries, and retry delay settings. If the lock cannot be acquired after all retries, a {@code LockAcquisitionException} is thrown.
	 * </p>
	 *
	 * @param key the unique identifier for the lock
	 * @param task the task to execute while holding the lock
	 * @return the result of the executed task
	 * @throws LockAcquisitionException if the lock cannot be acquired after all retry attempts
	 */
	public <T> T executeWithLock(String key, Supplier<T> task) {
		return executeWithLock(key, DEFAULT_TIMEOUT, DEFAULT_MAX_RETRIES, DEFAULT_RETRY_DELAY, task);
	}

	/****
	 * Executes the given task within a distributed lock identified by the specified key and timeout, using default retry settings.
	 *
	 * @param key the unique identifier for the distributed lock
	 * @param timeout the maximum duration to hold the lock
	 * @param task the task to execute while holding the lock
	 * @return the result of the executed task
	 * @throws IllegalArgumentException if the key is null or blank
	 * @throws LockAcquisitionException if the lock cannot be acquired after all retries
	 */
	public <T> T executeWithLock(String key, Duration timeout, Supplier<T> task) {
		return executeWithLock(key, timeout, DEFAULT_MAX_RETRIES, DEFAULT_RETRY_DELAY, task);
	}

	/**
	 * Executes the given task within a distributed lock using the specified key, timeout, and maximum retries.
	 *
	 * @param key the unique identifier for the lock
	 * @param timeout the duration to hold the lock before it expires
	 * @param maxRetries the maximum number of attempts to acquire the lock
	 * @param task the task to execute if the lock is acquired
	 * @return the result of the executed task
	 * @throws IllegalArgumentException if the key is null or blank
	 * @throws LockAcquisitionException if the lock cannot be acquired after all retries
	 */
	public <T> T executeWithLock(String key, Duration timeout, int maxRetries, Supplier<T> task) {
		return executeWithLock(key, timeout, maxRetries, DEFAULT_RETRY_DELAY, task);
	}

	/****
	 * Executes the given task within a distributed lock identified by the specified key.
	 *
	 * Acquires a distributed lock using the provided key and runs the given task with default timeout, retry count, and retry delay settings. The task is executed in a new, independent transaction to ensure isolation from any existing transactions. If the lock cannot be acquired after all retries, a {@link LockAcquisitionException} is thrown.
	 *
	 * @param key the unique identifier for the distributed lock
	 * @param task the task to execute within the lock context
	 * @throws LockAcquisitionException if the lock cannot be acquired after all retries
	 */
	public void executeWithLock(String key, Runnable task) {
		executeWithLock(key, DEFAULT_TIMEOUT, DEFAULT_MAX_RETRIES, DEFAULT_RETRY_DELAY, () -> {
			task.run();
			return null;
		});
	}

	/**
	 * Executes the given task within a distributed lock identified by the specified key and timeout.
	 *
	 * <p>
	 * Acquires a distributed lock for the provided key with the given timeout, then runs the task inside a new, independent transaction. If the lock cannot be acquired after the default number of retries, a {@code LockAcquisitionException} is thrown.
	 * </p>
	 *
	 * @param key the unique identifier for the lock
	 * @param timeout the maximum duration to hold the lock
	 * @param task the operation to execute while holding the lock
	 *
	 * @throws IllegalArgumentException if {@code key} is null or blank
	 * @throws LockAcquisitionException if the lock cannot be acquired after all retries
	 */
	public void executeWithLock(String key, Duration timeout, Runnable task) {
		executeWithLock(key, timeout, DEFAULT_MAX_RETRIES, DEFAULT_RETRY_DELAY, () -> {
			task.run();
			return null;
		});
	}

	/**
	 * Executes the given task within a distributed lock, retrying lock acquisition up to the specified maximum attempts.
	 *
	 * @param key the unique identifier for the distributed lock
	 * @param timeout the maximum duration to hold the lock
	 * @param maxRetries the maximum number of lock acquisition attempts
	 * @param task the task to execute once the lock is acquired
	 *
	 * @throws IllegalArgumentException if the key is null or blank
	 * @throws LockAcquisitionException if the lock cannot be acquired after all retries
	 */
	public void executeWithLock(String key, Duration timeout, int maxRetries, Runnable task) {
		executeWithLock(key, timeout, maxRetries, DEFAULT_RETRY_DELAY, () -> {
			task.run();
			return null;
		});
	}

	/**
	 * Executes the given task within a distributed lock, using the specified timeout, retry count, and retry delay.
	 *
	 * <p>
	 * Acquires a distributed lock identified by {@code key}, retrying up to {@code maxRetries} times with the given {@code retryDelay} between attempts. If the lock is acquired, the {@code task} is executed within a new transaction to ensure isolation. The lock is always released after execution.
	 * </p>
	 *
	 * @param key the unique identifier for the lock
	 * @param timeout the maximum duration to hold the lock
	 * @param maxRetries the maximum number of lock acquisition attempts
	 * @param retryDelay the delay between retries
	 * @param task the task to execute while holding the lock
	 *
	 * @throws IllegalArgumentException if {@code key} is null or blank
	 * @throws LockAcquisitionException if the lock cannot be acquired after all retries
	 */
	public void executeWithLock(String key, Duration timeout, int maxRetries, Duration retryDelay, Runnable task) {
		executeWithLock(key, timeout, maxRetries, retryDelay, () -> {
			task.run();
			return null;
		});
	}

	/**
	 * Attempts to acquire a distributed lock for the specified key with the given lock value and timeout.
	 *
	 * @param key the identifier for the lock
	 * @param lockValue the unique value representing the lock owner
	 * @param timeout the duration before the lock expires automatically
	 * @return true if the lock was successfully acquired; false otherwise
	 */
	private boolean acquireLock(String key, String lockValue, Duration timeout) {
		return lockManager.acquireLock(key, lockValue, timeout);
	}

	/**
	 * Attempts to release the distributed lock identified by the given key and lock value.
	 * <p>
	 * Logs the outcome of the release operation and handles any exceptions internally.
	 *
	 * @param key the lock key
	 * @param lockValue the unique value associated with the lock
	 */
	private void releaseLock(String key, String lockValue) {
		try {
			boolean released = lockManager.releaseLock(key, lockValue);
			if (released) {
				log.debug("[DistributedLock] 락 해제 성공 - 키: {}", key);
			} else {
				log.warn("[DistributedLock] 락 해제 실패 - 키: {}", key);
			}
		} catch (Exception e) {
			log.error("[DistributedLock] 락 해제 중 오류 발생 - 키: {}", key, e);
		}
	}

	/**
	 * Waits for a calculated delay before retrying lock acquisition, using exponential backoff and random jitter.
	 *
	 * @param baseRetryDelay the base duration for retry delay
	 * @param attempt the current retry attempt number (zero-based)
	 * @throws RuntimeException if the thread is interrupted while waiting
	 */
	private void waitBeforeRetry(Duration baseRetryDelay, int attempt) {
		try {
			long delayMillis = (long)(baseRetryDelay.toMillis() * Math.pow(1.5, attempt));
			long jitter = ThreadLocalRandom.current().nextLong(delayMillis / 4);

			Thread.sleep(delayMillis + jitter);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("락 획득 대기 중, 인터럽트 발생", e);
		}
	}
}
