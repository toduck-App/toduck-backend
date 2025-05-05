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
	 * 분산 락을 획득하고 작업을 실행합니다.
	 * 내부적으로 REQUIRES_NEW 전파 옵션을 사용하여 새로운 트랜잭션을 생성하여 작업을 실행합니다.
	 * 새 트랜잭션은 기존 트랜잭션과 독립적으로 실행되므로, 락 획득/해제 로직과 비즈니스 로직 간의
	 * 트랜잭션 격리가 보장됩니다.
	 *
	 * <p><strong>사용 시 주의사항:</strong>
	 * <ul>
	 *   <li>호출하는 메서드에 이미 트랜잭션이 있는 경우, 이 메서드 내부의 작업은 독립적인 새 트랜잭션으로 실행됩니다.</li>
	 *   <li>따라서 외부 트랜잭션이 롤백되어도 이 메서드 내부에서 실행된 작업은 롤백되지 않습니다.</li>
	 *   <li>반대로 내부 작업 중 예외가 발생해도 외부 트랜잭션은 영향받지 않고 독립적으로 커밋될 수 있습니다.</li>
	 *   <li>작업 실행 후 락이 자동으로 해제되므로 별도의 락 해제 로직을 구현할 필요가 없습니다.</li>
	 * </ul>
	 *
	 * <p><strong>테스트 시 주의사항:</strong>
	 * <ul>
	 *   <li>테스트에서 외부 트랜잭션 내에서 엔티티를 저장한 후 분산 락을 사용하면,
	 *       새 트랜잭션에서는 아직 커밋되지 않은 엔티티를 볼 수 없어 테스트가 실패할 수 있습니다.</li>
	 *   <li>JUnit 테스트의 @Transactional 롤백도 내부 트랜잭션에 적용되지 않아 데이터가 남을 수 있습니다.</li>
	 *   <li>해결책: @Transactional(propagation = Propagation.NEVER)로 테스트 트랜잭션을 비활성화하고,
	 *       테스트에서 명시적으로 데이터를 저장(flush)한 후 분산 락을 사용하며, @AfterEach에서 데이터를 정리하세요.</li>
	 * </ul>
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
		return lockManager.acquireLock(key, lockValue, timeout);
	}

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
