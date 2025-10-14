package im.toduck.domain.events.social.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "소셜 이벤트 참여 확인 응답")
public record EventsSocialCheckResponse(
	@Schema(description = "참여 여부 확인 응답")
	boolean participated
) {
	public static EventsSocialCheckResponse toEventsSocialCheckResponse(boolean participated) {
		return EventsSocialCheckResponse.builder()
			.participated(participated)
			.build();
	}
}
