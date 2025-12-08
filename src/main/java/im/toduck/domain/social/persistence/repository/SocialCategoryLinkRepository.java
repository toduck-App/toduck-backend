package im.toduck.domain.social.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.entity.SocialCategoryLink;

public interface SocialCategoryLinkRepository extends JpaRepository<SocialCategoryLink, Long> {
	void deleteAllBySocial(Social socialBoard);

	List<SocialCategoryLink> findAllBySocial(Social socialBoard);

	@Query("SELECT scl FROM SocialCategoryLink scl "
		+ "JOIN FETCH scl.socialCategory "
		+ "WHERE scl.social.id IN :socialIds AND scl.deletedAt IS NULL")
	List<SocialCategoryLink> findAllBySocialIdInWithCategory(@Param("socialIds") List<Long> socialIds);
}
