package im.toduck.domain.events.detail.domain.usecase;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.events.detail.common.mapper.EventsDetailMapper;
import im.toduck.domain.events.detail.domain.service.EventsDetailService;
import im.toduck.domain.events.detail.persistence.entity.EventsDetail;
import im.toduck.domain.events.detail.persistence.entity.EventsDetailImg;
import im.toduck.domain.events.detail.presentation.dto.request.EventsDetailCreateRequest;
import im.toduck.domain.events.detail.presentation.dto.request.EventsDetailUpdateRequest;
import im.toduck.domain.events.detail.presentation.dto.response.EventsDetailListResponse;
import im.toduck.domain.events.detail.presentation.dto.response.EventsDetailResponse;
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
public class EventsDetailUseCase {
	private final UserService userService;
	private final EventsDetailService eventsDetailService;

	@Transactional(readOnly = true)
	public EventsDetailListResponse getEventsDetails(final Long userId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		List<EventsDetailResponse> eventsDetails = eventsDetailService.getEventsDetail();

		return EventsDetailMapper.toListEventsDetailResponse(eventsDetails);
	}

	@Transactional
	public EventsDetail createEventsDetail(final EventsDetailCreateRequest request, final Long userId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		boolean exists = eventsDetailService.existsByEventsId(request.eventsId());
		if (exists) {
			throw CommonException.from(ExceptionCode.DUPLICATE_EVENTS_DETAIL);
		}

		EventsDetail eventsDetail = eventsDetailService.createEventsDetail(request);
		eventsDetailService.addEventsDetailImages(eventsDetail, request.eventsDetailImgs());

		return eventsDetail;
	}

	@Transactional
	public EventsDetail updateEventsDetail(
		final Long eventsDetailId,
		final EventsDetailUpdateRequest request,
		final Long userId
	) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		EventsDetail eventsDetail = eventsDetailService.getEventsDetailById(eventsDetailId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_EVENTS_DETAIL));

		eventsDetailService.updateEventsDetail(request, eventsDetail);
		return eventsDetail;
	}

	@Transactional
	public void deleteEventsDetail(final Long eventsDetailId, final Long userId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		EventsDetail eventsDetail = eventsDetailService.findById(eventsDetailId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_EVENTS_DETAIL));

		eventsDetail.getEventsDetailImgs().forEach(EventsDetailImg::softDelete);

		eventsDetailService.deleteEventsDetail(eventsDetail);
	}
}
