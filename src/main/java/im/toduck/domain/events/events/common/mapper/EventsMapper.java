package im.toduck.domain.events.events.common.mapper;

import java.util.List;

import im.toduck.domain.events.events.persistence.entity.Events;
import im.toduck.domain.events.events.presentation.dto.EventsCreateRequest;
import im.toduck.domain.events.events.presentation.dto.EventsListResponse;
import im.toduck.domain.events.events.presentation.dto.EventsResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventsMapper {
	public static EventsListResponse toListEventsResponse(List<EventsResponse> events) {
		return EventsListResponse.toListEventsResponse(events);
	}

	public static EventsResponse fromEvents(Events events) {
		return new EventsResponse(
			events.getId(),
			events.getEventName(),
			events.getStartAt(),
			events.getEndAt(),
			events.getThumbUrl(),
			events.getAppVersion()
		);
	}

	public static Events toEvents(EventsCreateRequest request) {
		return Events.builder()
			.eventName(request.eventName())
			.startAt(request.startAt())
			.endAt(request.endAt())
			.thumbUrl(request.thumbUrl())
			.appVersion(request.appVersion())
			.build();
	}
}
