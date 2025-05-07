package im.toduck.global.lock;

import java.util.function.Supplier;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionExecutor {

	/**
	 * Executes the given task within a new transaction context and returns its result.
	 *
	 * @param supplier the task to execute
	 * @param <T> the type of the result
	 * @return the result of the executed task
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public <T> T executeInNewTransaction(Supplier<T> supplier) {
		return supplier.get();
	}
}
