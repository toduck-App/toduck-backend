package im.toduck.global.lock;

public class LockAcquisitionException extends RuntimeException {

	public LockAcquisitionException(String lockKey, int attempts) {
		super(String.format("락 획득 실패 - 키: %s, 시도횟수: %d", lockKey, attempts));
	}
}
