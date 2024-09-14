package im.toduck.global.util;

import java.util.List;
import java.util.function.Function;

import im.toduck.global.presentation.dto.response.CursorPaginationResponse;

public class PaginationUtil {
	private static final int ADDITIONAL_ITEM_FOR_NEXT_PAGE = 1;
	private static final int LAST_ITEM_OFFSET = 1;
	public static final int FIRST_PAGE_INDEX = 0;

	/**
	 * 실제 사용할 페이지네이션 limit 값을 결정하는 메서드.
	 * null일 경우 기본값을 사용.
	 *
	 * @param limit        요청된 limit 값.
	 * @param defaultLimit 기본 limit 값.
	 * @return 실제 사용할 limit 값.
	 */
	public static int resolveLimit(Integer limit, int defaultLimit) {
		return (limit != null) ? limit : defaultLimit;
	}

	/**
	 * 페이지네이션에서 실제로 가져올 데이터의 수를 계산합니다.
	 * 추가 항목(ADDITIONAL_ITEM_FOR_NEXT_PAGE)을 포함하여 다음 페이지의 유무를 결정하는 데 사용됩니다.
	 *
	 * @param limit 요청한 데이터 개수 (한 페이지에 표시할 항목 수)
	 * @return 추가 항목을 고려한 페칭할 데이터 개수
	 */
	public static int calculateTotalFetchSize(int limit) {
		return limit + ADDITIONAL_ITEM_FOR_NEXT_PAGE;
	}

	/**
	 * 더 많은 페이지가 있는지 여부를 확인하는 메서드
	 *
	 * @param itemList 항목 리스트
	 * @param limit    한 페이지에 표시할 항목 수
	 * @return 더 많은 항목이 있는지 여부
	 */
	public static <T> boolean hasMore(List<T> itemList, int limit) {
		return itemList.size() > limit;
	}

	/**
	 * 다음 페이지를 위한 커서를 가져오는 메서드
	 *
	 * @param hasMore    더 많은 항목이 있는지 여부
	 * @param items      항목 리스트
	 * @param limit      한 페이지에 표시할 항목 수
	 * @param getIdFunction 항목의 ID를 가져오는 함수
	 * @return 다음 커서 값
	 */
	public static <T> Long getNextCursor(boolean hasMore, List<T> items, int limit, Function<T, Long> getIdFunction) {
		if (!hasMore || items.isEmpty()) {
			return null;
		}
		return getIdFunction.apply(items.get(limit - LAST_ITEM_OFFSET));
	}

	/**
	 * 커서 기반 페이지네이션 응답을 생성하는 메서드
	 *
	 * @param hasMore   더 많은 항목이 있는지 여부
	 * @param nextCursor 다음 커서 값
	 * @param results   결과 리스트
	 * @return 커서 페이지네이션 응답
	 */
	public static <T> CursorPaginationResponse<T> toCursorPaginationResponse(
		boolean hasMore,
		Long nextCursor,
		List<T> results
	) {
		return CursorPaginationResponse.<T>builder()
			.hasMore(hasMore)
			.nextCursor(nextCursor)
			.results(results)
			.build();
	}
}
