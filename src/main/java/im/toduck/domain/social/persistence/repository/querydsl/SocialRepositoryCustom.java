package im.toduck.domain.social.persistence.repository.querydsl;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.entity.SocialCategory;
import im.toduck.domain.social.persistence.entity.SocialImageFile;

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

	Map<Long, List<SocialImageFile>> findImageFilesBySocialIds(List<Long> socialIds);

	Map<Long, Integer> countCommentsBySocialIds(List<Long> socialIds);

	Map<Long, Boolean> findLikesBySocialIdsAndUserId(List<Long> socialIds, Long userId);

	Map<Long, List<SocialCategory>> findCategoriesBySocialIds(List<Long> socialIds);
}
