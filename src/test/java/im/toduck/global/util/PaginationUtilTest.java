package im.toduck.global.util;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PaginationUtilTest {

	@Nested
	@DisplayName("resolveLimit 메서드 테스트")
	class ResolveLimitTest {

		@Test
		void limit이_null일_경우_defaultLimit을_반환한다() {
			int defaultLimit = 10;
			Integer limit = null;

			int resolvedLimit = PaginationUtil.resolveLimit(limit, defaultLimit);

			assertThat(resolvedLimit).isEqualTo(defaultLimit);
		}

		@Test
		void limit이_null이_아닌_경우_해당_limit을_반환한다() {
			int defaultLimit = 10;
			Integer limit = 5;

			int resolvedLimit = PaginationUtil.resolveLimit(limit, defaultLimit);

			assertThat(resolvedLimit).isEqualTo(limit);
		}
	}

	@Nested
	@DisplayName("calculateTotalFetchSize 메서드 테스트")
	class CalculateTotalFetchSizeTest {

		@Test
		void 총_fetch_크기는_limit보다_1만큼_더_크다() {
			int limit = 5;
			int fetchSize = PaginationUtil.calculateTotalFetchSize(limit);

			assertThat(fetchSize).isEqualTo(limit + 1);
		}
	}

	@Nested
	@DisplayName("hasMore 메서드 테스트")
	class HasMoreTest {

		@Test
		void 리스트_크기가_limit보다_큰_경우_true를_반환한다() {
			List<Integer> items = List.of(1, 2, 3, 4, 5, 6);
			int limit = 5;

			boolean hasMore = PaginationUtil.hasMore(items, limit);

			assertThat(hasMore).isTrue();
		}

		@Test
		void 리스트_크기가_limit_이하일_경우_false를_반환한다() {
			List<Integer> items = List.of(1, 2, 3, 4, 5);
			int limit = 5;

			boolean hasMore = PaginationUtil.hasMore(items, limit);

			assertThat(hasMore).isFalse();
		}
	}

	@Nested
	@DisplayName("getNextCursor 메서드 테스트")
	class GetNextCursorTest {

		@Test
		void hasMore가_true일_때_마지막_항목의_ID를_반환한다() {
			List<TestItem> items = List.of(
				new TestItem(1L),
				new TestItem(2L),
				new TestItem(3L),
				new TestItem(4L),
				new TestItem(5L)
			);
			boolean hasMore = true;
			int limit = 5;

			Long nextCursor = PaginationUtil.getNextCursor(hasMore, items, limit, TestItem::id);

			assertThat(nextCursor).isEqualTo(5L);
		}

		@Test
		void hasMore가_false일_때_null을_반환한다() {
			List<TestItem> items = List.of(
				new TestItem(1L),
				new TestItem(2L),
				new TestItem(3L)
			);
			boolean hasMore = false;
			int limit = 3;

			Long nextCursor = PaginationUtil.getNextCursor(hasMore, items, limit, TestItem::id);

			assertThat(nextCursor).isNull();
		}
	}

	record TestItem(Long id) {
	}
}
