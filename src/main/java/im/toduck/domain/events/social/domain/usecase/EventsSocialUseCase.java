package im.toduck.domain.events.social.domain.usecase;

import java.time.LocalDate;

import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.events.social.common.mapper.EventsSocialMapper;
import im.toduck.domain.events.social.domain.service.EventsSocialService;
import im.toduck.domain.events.social.presentation.dto.request.EventsSocialRequest;
import im.toduck.domain.events.social.presentation.dto.response.EventsSocialCheckResponse;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.annotation.UseCase;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;

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
	public void createEventsSocial(final EventsSocialRequest request, final Long userId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		boolean checkEventsSocial = eventsSocialService.getEventsSocialByDate(request.date(), userId);

		if (checkEventsSocial) {
			throw CommonException.from(ExceptionCode.ALREADY_EXISTS_EVENTSSOCIAL);
		} else {
			eventsSocialService.saveEventsSocial(request, userId);
		}
	}
}
