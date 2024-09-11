package im.toduck.global.presentation.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record CursorPaginationResponse<T>(
	@Schema(description = "다음 페이지가 있는지 여부", example = "true") boolean hasMore,
	@Schema(description = "다음 페이지를 위한 커서", example = "1") Long nextCursor,
	@Schema(description = "결과 리스트", example = "{}") List<T> results
) {
}
