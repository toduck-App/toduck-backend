package im.toduck.domain.events.events.domain.usecase;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.events.events.common.mapper.EventsMapper;
import im.toduck.domain.events.events.domain.service.EventsService;
import im.toduck.domain.events.events.persistence.entity.Events;
import im.toduck.domain.events.events.presentation.dto.request.EventsCreateRequest;
import im.toduck.domain.events.events.presentation.dto.request.EventsUpdateRequest;
import im.toduck.domain.events.events.presentation.dto.response.EventsListResponse;
import im.toduck.domain.events.events.presentation.dto.response.EventsResponse;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.annotation.UseCase;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class EventsUseCase {
	private final UserService userService;
	private final EventsService eventsService;

	@Transactional(readOnly = true)
	public EventsListResponse getEvents(final Long userId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		List<EventsResponse> events = eventsService.getEvents();

		return EventsMapper.toListEventsResponse(events);
	}

	@Transactional
	public Events createEvents(final EventsCreateRequest request, final Long userId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		return eventsService.createEvents(request);
	}

	@Transactional
	public void updateEvents(
		final Long eventsId,
		final EventsUpdateRequest request,
		final Long userId
	) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Events events = eventsService.getEventsById(eventsId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_EVENTS));

		eventsService.updateEvents(events, request);
	}

	@Transactional
	public void deleteEvents(final Long eventsId, final Long userId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Events events = eventsService.getEventsById(eventsId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_EVENTS));

		eventsService.deleteEvents(events);
	}
}
