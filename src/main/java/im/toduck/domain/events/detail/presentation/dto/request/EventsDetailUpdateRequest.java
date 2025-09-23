package im.toduck.domain.events.detail.presentation.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "이벤트 디테일 수정 요청 DTO")
public record EventsDetailUpdateRequest(
	@Schema(description = "이벤트 ID", example = "1")
	Long eventsId,

	@Schema(description = "연결 URL", example = "toduck://createPost")
	String routingUrl,

	@Schema(description = "변경된 이미지 URL 목록", example = "[\"https://cdn.app/image1.jpg\"]")
	List<String> eventsDetailImgs
) {
}
