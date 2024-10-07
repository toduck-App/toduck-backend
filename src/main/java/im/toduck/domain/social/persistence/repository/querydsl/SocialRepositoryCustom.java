package im.toduck.domain.social.persistence.repository.querydsl;

import java.util.List;

import org.springframework.data.domain.Pageable;

import im.toduck.domain.social.persistence.entity.Social;

public interface SocialRepositoryCustom {
	List<Social> findByIdBeforeOrderByIdDescExcludingBlocked(Long cursor, Long currentUserId, Pageable pageable);

	List<Social> findLatestSocialsExcludingBlocked(Long currentUserId, Pageable pageable);
}
