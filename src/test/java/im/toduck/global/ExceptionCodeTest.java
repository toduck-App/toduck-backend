package im.toduck.global;

import static org.assertj.core.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import im.toduck.global.exception.ExceptionCode;

class ExceptionCodeTest {
	@Test
	void 에러코드는_중복되지_않는다() {
		// given when
		Set<Integer> codes = new HashSet<>();
		for (ExceptionCode code : ExceptionCode.values()) {
			codes.add(code.getErrorCode());
		}

		assertThat(codes).hasSize(ExceptionCode.values().length);
	}
}
