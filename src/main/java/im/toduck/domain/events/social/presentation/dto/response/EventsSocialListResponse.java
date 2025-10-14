package im.toduck.domain.events.social.presentation.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "소셜 이벤트 목록 응답")
@Builder
public record EventsSocialListResponse(
	@Schema(description = "소셜 이벤트 목록")
	List<EventsSocialResponse> eventsSocialDtos
) {
	public static EventsSocialListResponse toListEventsSocialResponse(List<EventsSocialResponse> eventsSocials) {
		return EventsSocialListResponse.builder()
			.eventsSocialDtos(eventsSocials)
			.build();
	}
}
