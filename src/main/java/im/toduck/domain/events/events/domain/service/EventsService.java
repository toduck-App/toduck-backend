package im.toduck.domain.events.events.domain.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.events.events.common.mapper.EventsMapper;
import im.toduck.domain.events.events.persistence.entity.Events;
import im.toduck.domain.events.events.persistence.repository.EventsRepository;
import im.toduck.domain.events.events.presentation.dto.request.EventsCreateRequest;
import im.toduck.domain.events.events.presentation.dto.request.EventsUpdateRequest;
import im.toduck.domain.events.events.presentation.dto.response.EventsResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventsService {
	private final EventsRepository eventsRepository;

	@Transactional(readOnly = true)
	public List<EventsResponse> getEvents() {
		List<Events> events = eventsRepository.findAllByOrderByIdAsc();
		return events.stream()
			.map(EventsMapper::fromEvents)
			.toList();
	}

	@Transactional
	public Events createEvents(final EventsCreateRequest request) {
		Events events = EventsMapper.toEvents(request);
		eventsRepository.save(events);
		return events;
	}

	@Transactional(readOnly = true)
	public Optional<Events> getEventsById(Long eventsId) {
		return eventsRepository.findById(eventsId);
	}

	@Transactional
	public void updateEvents(final Events events, final EventsUpdateRequest request) {
		if (request.eventName() != null) {
			events.updateEventName(request.eventName());
		}
		if (request.startAt() != null) {
			events.updateStartAt(request.startAt());
		}
		if (request.endAt() != null) {
			events.updateEndAt(request.endAt());
		}
		if (request.thumbUrl() != null) {
			events.updateThumbUrl(request.thumbUrl());
		}
		if (request.appVersion() != null) {
			events.updateAppVersion(request.appVersion());
		}
	}

	@Transactional
	public void deleteEvents(final Events events) {
		eventsRepository.delete(events);
	}
}
