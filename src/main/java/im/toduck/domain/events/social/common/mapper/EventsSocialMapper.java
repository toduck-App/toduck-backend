package im.toduck.domain.events.social.common.mapper;

import im.toduck.domain.events.social.presentation.dto.response.EventsSocialCheckResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventsSocialMapper {
	public static EventsSocialCheckResponse toEventsSocialCheckResponse(boolean participated) {
		return EventsSocialCheckResponse.toEventsSocialCheckResponse(participated);
	}
}
