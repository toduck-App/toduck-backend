package im.toduck.domain.events.detail.presentation.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "이벤트 디테일 응답")
public record EventsDetailResponse(
	@Schema(description = "이벤트 디테일 ID", example = "1")
	Long eventsDetailId,

	@Schema(description = "이벤트 ID", example = "1")
	Long eventsId,

	@Schema(description = "연결 url", example = "toduck://createPost")
	String routingUrl,

	@Schema(description = "버튼 표시 여부", example = "true")
	Boolean buttonVisible,

	@Schema(description = "버튼 내용", example = "당첨 확인하기")
	String buttonText,

	@Schema(description = "이벤트 디테일 이미지 url 목록", example = "[\"https://cdn.toduck.app/image1.jpg\"]")
	List<String> eventsDetailImgUrl
) {

}
