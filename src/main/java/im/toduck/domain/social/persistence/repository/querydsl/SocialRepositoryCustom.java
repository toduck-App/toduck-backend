package im.toduck.domain.social.persistence.repository.querydsl;

import java.util.List;

import org.springframework.data.domain.Pageable;

import im.toduck.domain.social.persistence.entity.Social;

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
		Pageable pageable
	);

	List<Social> findUserSocials(
		Long profileUserId,
		Long cursor,
		Pageable pageable
	);
}
