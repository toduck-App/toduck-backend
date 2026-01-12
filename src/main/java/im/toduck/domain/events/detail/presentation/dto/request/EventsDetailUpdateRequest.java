package im.toduck.domain.events.detail.presentation.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "이벤트 디테일 수정 요청 DTO")
public record EventsDetailUpdateRequest(
	@Schema(description = "이벤트 ID", example = "1")
	Long eventsId,

	@Schema(description = "연결 URL", example = "toduck://createPost")
	String routingUrl,

	@Schema(description = "버튼 표시 여부", example = "true")
	Boolean buttonVisible,

	@Size(max = 63, message = "버튼 내용은 63자를 초과할 수 없습니다.")
	@Schema(description = "버튼 내용", example = "당첨 확인하기")
	String buttonText,

	@Schema(description = "변경된 이미지 URL 목록", example = "[\"https://cdn.app/image1.jpg\"]")
	List<String> eventsDetailImgs
) {
}
