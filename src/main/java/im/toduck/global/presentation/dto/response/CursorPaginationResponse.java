package im.toduck.global.presentation.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record CursorPaginationResponse<T>(
	boolean hasMore,
	Long nextCursor,
	List<T> results
) {
}
