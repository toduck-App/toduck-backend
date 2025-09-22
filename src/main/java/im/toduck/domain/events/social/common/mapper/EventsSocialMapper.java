package im.toduck.domain.events.social.common.mapper;

import java.util.List;

import im.toduck.domain.events.social.persistence.entity.EventsSocial;
import im.toduck.domain.events.social.presentation.dto.response.EventsSocialCheckResponse;
import im.toduck.domain.events.social.presentation.dto.response.EventsSocialListResponse;
import im.toduck.domain.events.social.presentation.dto.response.EventsSocialResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventsSocialMapper {
	public static EventsSocialCheckResponse toEventsSocialCheckResponse(boolean participated) {
		return EventsSocialCheckResponse.toEventsSocialCheckResponse(participated);
	}

	public static EventsSocialResponse fromEventsSocials(EventsSocial eventsSocial) {
		return new EventsSocialResponse(
			eventsSocial.getId(),
			eventsSocial.getSocial().getTitle(),
			eventsSocial.getUser().getNickname(),
			eventsSocial.getPhone(),
			eventsSocial.getDate()
		);
	}

	public static EventsSocialListResponse toListEventsSocialResponse(List<EventsSocialResponse> eventsSocials) {
		return EventsSocialListResponse.toListEventsSocialResponse(eventsSocials);
	}
}
