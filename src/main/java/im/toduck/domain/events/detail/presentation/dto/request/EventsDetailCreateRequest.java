package im.toduck.domain.events.detail.presentation.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "이벤트 디테일 생성 요청 DTO")
public record EventsDetailCreateRequest(
	@NotNull(message = "이벤트 ID는 비어있을 수 없습니다.")
	@Schema(description = "이벤트 ID", example = "1")
	Long eventsId,

	@Size(max = 1023, message = "라우팅 url은 1023자를 초과할 수 없습니다.")
	@Schema(description = "라우팅 url", example = "toduck://createPost")
	String routingUrl,

	@NotNull(message = "버튼 표시 여부는 비어있을 수 없습니다.")
	@Schema(description = "버튼 표시 여부", example = "true")
	Boolean buttonVisible,

	@Size(max = 63, message = "버튼 내용은 63자를 초과할 수 없습니다.")
	@Schema(description = "버튼 내용", example = "당첨 확인하기")
	String buttonText,

	@Schema(description = "이벤트 디테일 사진 URL 목록", example = "[\"https://cdn.toduck.app/image1.jpg\"]")
	List<String> eventsDetailImgs
) {

}
