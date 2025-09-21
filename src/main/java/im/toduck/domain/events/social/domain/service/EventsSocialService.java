package im.toduck.domain.events.social.domain.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.events.social.persistence.entity.EventsSocial;
import im.toduck.domain.events.social.persistence.repository.EventsSocialRepository;
import im.toduck.domain.events.social.presentation.dto.request.EventsSocialRequest;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.repository.SocialRepository;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventsSocialService {
	private final EventsSocialRepository eventsSocialRepository;

	private final SocialRepository socialRepository;

	@Transactional(readOnly = true)
	public boolean getEventsSocialByDate(final LocalDate date, final Long userId) {
		return eventsSocialRepository.findByDateAndUserId(date, userId).isPresent();
	}

	@Transactional
	public void saveEventsSocial(final EventsSocialRequest request, final Long userId) {
		Optional<Social> socialOptional = socialRepository.findById(request.socialId());
		if (!socialOptional.isPresent()) {
			throw CommonException.from(ExceptionCode.NOT_FOUND_SOCIAL_BOARD);
		}

		Social social = socialOptional.get();
		if (!social.getUser().getId().equals(userId)) {
			throw CommonException.from(ExceptionCode.UNAUTHORIZED_ACCESS_EVENTSSOCIAL);
		}

		eventsSocialRepository.save(
			EventsSocial.builder()
				.social(social)
				.user(social.getUser())
				.phone(request.phone())
				.date(LocalDate.now())
				.build()
		);
	}
}
