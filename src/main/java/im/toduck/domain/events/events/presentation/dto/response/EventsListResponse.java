package im.toduck.domain.events.events.presentation.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "이벤트 목록 응답")
@Builder
public record EventsListResponse(
	@Schema(description = "이벤트 목록")
	List<EventsResponse> eventsDtos
) {
	public static EventsListResponse toListEventsResponse(List<EventsResponse> events) {
		return EventsListResponse.builder()
			.eventsDtos(events)
			.build();
	}
}
