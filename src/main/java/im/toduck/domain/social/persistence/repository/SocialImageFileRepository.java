package im.toduck.domain.social.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.entity.SocialImageFile;

public interface SocialImageFileRepository extends JpaRepository<SocialImageFile, Long> {
	List<SocialImageFile> findAllBySocial(Social socialBoard);

	void deleteAllBySocial(Social socialBoard);
}
