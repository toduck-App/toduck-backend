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
		// 있는지 확인 -> 없으면 저장, 있으면 오류 반환
		if (checkEventsSocial) {
			throw CommonException.from(ExceptionCode.ALREADY_EXISTS_EVENTSSOCIAL);
		} else {
			eventsSocialService.saveEventsSocial(request, userId);
		}
		// 만약 참여된 게시글이 삭제 될 경우 값 지우고 소셜 게시글 중에 같은 날에 작성된 글이 있는지 확인하고 다시 저장??
	}
}
