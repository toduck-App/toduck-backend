package im.toduck.domain.social.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.entity.SocialCategoryLink;

public interface SocialCategoryLinkRepository extends JpaRepository<SocialCategoryLink, Long> {
	void deleteAllBySocial(Social socialBoard);

	List<SocialCategoryLink> findAllBySocial(Social socialBoard);
}
