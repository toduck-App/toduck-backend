package im.toduck.global;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import im.toduck.global.exception.ExceptionCode;

class ExceptionCodeTest {

	private static final int CATEGORY_DIVISOR = 100;
	private static final int MIN_ERROR_CODE_DIGITS = 10000;
	private static final int MAX_ERROR_CODE_DIGITS = 99999;
	private static final int MIN_LAST_DIGITS = 1;
	private static final int MAX_LAST_DIGITS = 99;

	@Test
	void 에러코드는_중복되지_않는다() {
		// given
		Set<Integer> codes = new HashSet<>();

		// when
		for (ExceptionCode code : ExceptionCode.values()) {
			codes.add(code.getErrorCode());
		}

		// then
		assertThat(codes).hasSize(ExceptionCode.values().length);
	}

	@Test
	void 에러코드는_카테고리별로_순차적이다() {
		// given
		Map<Integer, List<Integer>> categoryMap = Arrays.stream(ExceptionCode.values())
			.collect(Collectors.groupingBy(
				code -> code.getErrorCode() / CATEGORY_DIVISOR,
				Collectors.mapping(ExceptionCode::getErrorCode, Collectors.toList())
			));

		// when & then
		categoryMap.forEach((category, errorCodes) -> {
			Collections.sort(errorCodes);

			for (int i = 0; i < errorCodes.size() - 1; i++) {
				int current = errorCodes.get(i);
				int next = errorCodes.get(i + 1);

				assertThat(next).isEqualTo(current + 1);
			}
		});
	}

	@Test
	void 모든_에러코드는_5자리_숫자이다() {
		// when & then
		for (ExceptionCode code : ExceptionCode.values()) {
			assertThat(code.getErrorCode()).isBetween(MIN_ERROR_CODE_DIGITS, MAX_ERROR_CODE_DIGITS);
		}
	}

	@Test
	void 모든_예외코드는_메시지를_가지고_있다() {
		// when & then
		for (ExceptionCode code : ExceptionCode.values()) {
			assertThat(code.getMessage()).isNotEmpty();
		}
	}

	@Test
	void 에러코드는_카테고리에_맞는_범위를_가진다() {
		// when & then
		for (ExceptionCode code : ExceptionCode.values()) {
			int lastDigits = code.getErrorCode() % CATEGORY_DIVISOR;
			assertThat(lastDigits).isBetween(MIN_LAST_DIGITS, MAX_LAST_DIGITS);
		}
	}
}
