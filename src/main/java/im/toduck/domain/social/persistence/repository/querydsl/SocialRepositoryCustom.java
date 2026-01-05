package im.toduck.domain.social.persistence.repository.querydsl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;

import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.global.persistence.projection.DailyCount;

public interface SocialRepositoryCustom {
	List<Social> findSocialsExcludingBlocked(
		Long cursor,
		Long currentUserId,
		List<Long> categoryIds,
		Pageable pageable
	);

	List<Social> searchSocialsExcludingBlocked(
		Long cursor,
		Long currentUserId,
		String keyword,
		List<Long> categoryIds,
		Pageable pageable
	);

	List<Social> findUserSocials(
		Long profileUserId,
		Long cursor,
		Pageable pageable
	);

	List<DailyCount> countByCreatedAtBetweenGroupByDate(
		final LocalDateTime startDateTime,
		final LocalDateTime endDateTime
	);
}
