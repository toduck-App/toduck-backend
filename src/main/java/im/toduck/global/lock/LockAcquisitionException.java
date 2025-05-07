package im.toduck.global.lock;

public class LockAcquisitionException extends RuntimeException {

	/**
		 * Constructs a new exception indicating failure to acquire a lock after a specified number of attempts.
		 *
		 * @param lockKey the identifier of the lock that could not be acquired
		 * @param attempts the number of attempts made to acquire the lock
		 */
	public LockAcquisitionException(String lockKey, int attempts) {
		super(String.format("락 획득 실패 - 키: %s, 시도횟수: %d", lockKey, attempts));
	}
}
