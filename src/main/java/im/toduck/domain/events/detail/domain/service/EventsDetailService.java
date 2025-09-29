package im.toduck.domain.events.detail.domain.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.events.detail.common.mapper.EventsDetailImgMapper;
import im.toduck.domain.events.detail.common.mapper.EventsDetailMapper;
import im.toduck.domain.events.detail.persistence.entity.EventsDetail;
import im.toduck.domain.events.detail.persistence.entity.EventsDetailImg;
import im.toduck.domain.events.detail.persistence.repository.EventsDetailImgRepository;
import im.toduck.domain.events.detail.persistence.repository.EventsDetailRepository;
import im.toduck.domain.events.detail.persistence.repository.querydsl.EventsDetailRepositoryCustom;
import im.toduck.domain.events.detail.presentation.dto.request.EventsDetailCreateRequest;
import im.toduck.domain.events.detail.presentation.dto.request.EventsDetailUpdateRequest;
import im.toduck.domain.events.detail.presentation.dto.response.EventsDetailResponse;
import im.toduck.domain.events.events.persistence.entity.Events;
import im.toduck.domain.events.events.persistence.repository.EventsRepository;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventsDetailService {
	private final EventsRepository eventsRepository;
	private final EventsDetailRepository eventsDetailRepository;
	private final EventsDetailImgRepository eventsDetailImgRepository;
	private final EventsDetailRepositoryCustom eventsDetailRepositoryCustom;

	@Transactional
	public EventsDetail createEventsDetail(final EventsDetailCreateRequest request) {
		Events events = eventsRepository.findById(request.eventsId())
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_EVENTS));

		EventsDetail eventsDetail = EventsDetailMapper.toEventsDetail(request, events);
		return eventsDetailRepository.save(eventsDetail);
	}

	@Transactional
	public void addEventsDetailImges(final EventsDetail eventsDetail, final List<String> imgUrls) {
		List<String> safeImgs = Optional.ofNullable(imgUrls).orElse(Collections.emptyList());

		List<EventsDetailImg> eventsDetailImgs = safeImgs.stream()
			.map(url -> EventsDetailImgMapper.toEventDetailImg(eventsDetail, url))
			.toList();
		eventsDetailImgRepository.saveAll(eventsDetailImgs);
	}

	@Transactional(readOnly = true)
	public List<EventsDetailResponse> getEventsDetail() {
		List<EventsDetail> eventsDetails = eventsDetailRepositoryCustom.findAllWithImgs();
		return eventsDetails.stream()
			.map(EventsDetailMapper::fromEventsDetail)
			.toList();
	}

	@Transactional(readOnly = true)
	public boolean existsByEventsId(final Long eventsId) {
		return eventsDetailRepository.existsByEventsId(eventsId);
	}

	@Transactional
	public Optional<EventsDetail> getEventsDetailById(final Long eventsDetailId) {
		return eventsDetailRepository.findById(eventsDetailId);
	}

	@Transactional
	public void updateEventsDetail(
		final EventsDetailUpdateRequest request,
		final EventsDetail eventsDetail
	) {
		if (request.eventsId() != null) {
			Events events = eventsRepository.findById(request.eventsId())
				.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_EVENTS));
			eventsDetail.updateEvents(events);
		}

		if (request.routingUrl() != null) {
			eventsDetail.updateRoutingUrl(request.routingUrl());
		}

		if (request.eventsDetailImgs() != null && !request.eventsDetailImgs().isEmpty()) {
			eventsDetailImgRepository.deleteAllByEventsDetail(eventsDetail);
			addEventsDetailImges(eventsDetail, request.eventsDetailImgs());
		} else {
			eventsDetailImgRepository.deleteAllByEventsDetail(eventsDetail);
		}
	}

	@Transactional
	public Optional<EventsDetail> findById(final Long eventsDetailId) {
		return eventsDetailRepository.findById(eventsDetailId);
	}

	@Transactional
	public void deleteEventsDetail(final EventsDetail eventsDetail) {
		eventsDetailRepository.delete(eventsDetail);
	}
}
