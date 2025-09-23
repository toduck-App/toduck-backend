package im.toduck.domain.events.detail.presentation.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "이벤트 디테일 목록 응답")
public record EventsDetailListResponse(
	@Schema(description = "이벤트 디테일 목록")
	List<EventsDetailResponse> eventsDetailDtos
) {
	public static EventsDetailListResponse toListEventsDetailResponse(
		final List<EventsDetailResponse> eventsDetails
	) {
		return EventsDetailListResponse.builder()
			.eventsDetailDtos(eventsDetails)
			.build();
	}
}
