package im.toduck.domain.events.detail.common.mapper;

import java.util.List;

import im.toduck.domain.events.detail.persistence.entity.EventsDetail;
import im.toduck.domain.events.detail.presentation.dto.request.EventsDetailCreateRequest;
import im.toduck.domain.events.detail.presentation.dto.response.EventsDetailListResponse;
import im.toduck.domain.events.detail.presentation.dto.response.EventsDetailResponse;
import im.toduck.domain.events.events.persistence.entity.Events;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventsDetailMapper {
	public static EventsDetail toEventsDetail(
		final EventsDetailCreateRequest request,
		final Events events
	) {
		return EventsDetail.builder()
			.events(events)
			.routingUrl(request.routingUrl())
			.build();
	}

	public static EventsDetailResponse fromEventsDetail(final EventsDetail eventsDetail) {
		return new EventsDetailResponse(
			eventsDetail.getId(),
			eventsDetail.getEvents().getId(),
			eventsDetail.getRoutingUrl(),
			eventsDetail.getEventsDetailImgs().stream()
				.map(EventsDetailImgMapper::fromEventDetailImg)
				.toList()
		);
	}

	public static EventsDetailListResponse toListEventsDetailResponse(
		final List<EventsDetailResponse> eventsDetails
	) {
		return EventsDetailListResponse.toListEventsDetailResponse(eventsDetails);
	}
}
