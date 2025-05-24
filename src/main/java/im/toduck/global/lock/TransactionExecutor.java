package im.toduck.global.lock;

import java.util.function.Supplier;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionExecutor {

	/**
	 * 새로운 트랜잭션 컨텍스트에서 주어진 작업을 실행합니다.
	 *
	 * @param supplier 실행할 작업
	 * @return 작업 실행 결과
	 * @param <T> 반환 타입
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public <T> T executeInNewTransaction(Supplier<T> supplier) {
		return supplier.get();
	}
}
