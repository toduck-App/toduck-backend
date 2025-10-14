package im.toduck.domain.events.social.domain.usecase;

import java.time.LocalDate;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.events.social.common.mapper.EventsSocialMapper;
import im.toduck.domain.events.social.domain.service.EventsSocialService;
import im.toduck.domain.events.social.persistence.entity.EventsSocial;
import im.toduck.domain.events.social.presentation.dto.request.EventsSocialRequest;
import im.toduck.domain.events.social.presentation.dto.response.EventsSocialCheckResponse;
import im.toduck.domain.events.social.presentation.dto.response.EventsSocialListResponse;
import im.toduck.domain.events.social.presentation.dto.response.EventsSocialResponse;
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
public class EventsSocialUseCase {
	private final UserService userService;
	private final EventsSocialService eventsSocialService;

	@Transactional(readOnly = true)
	public EventsSocialCheckResponse checkEventsSocial(final LocalDate date, final Long userId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		boolean checkEventsSocial = eventsSocialService.getEventsSocialByDate(date, userId);
		return EventsSocialMapper.toEventsSocialCheckResponse(checkEventsSocial);
	}

	@Transactional
	public void saveEventsSocial(final EventsSocialRequest request, final Long userId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		boolean checkEventsSocial = eventsSocialService.getEventsSocialByDate(request.date(), userId);

		if (checkEventsSocial) {
			throw CommonException.from(ExceptionCode.ALREADY_EXISTS_EVENTSSOCIAL);
		} else {
			eventsSocialService.saveEventsSocial(request, userId);
		}
	}

	@Transactional(readOnly = true)
	public EventsSocialListResponse getEventsSocial(final Long userId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		List<EventsSocialResponse> eventsSocials = eventsSocialService.getEventsSocials();

		return EventsSocialMapper.toListEventsSocialResponse(eventsSocials);
	}

	@Transactional
	public void deleteEvents(final Long eventsSocialId, final Long userId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		EventsSocial eventsSocial = eventsSocialService.getEventsSocialById(eventsSocialId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_EVENTS_SOCIAL));

		eventsSocialService.deleteEventsSocial(eventsSocial);
	}
}
