package im.toduck.domain.social.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.entity.SocialImageFile;

public interface SocialImageFileRepository extends JpaRepository<SocialImageFile, Long> {
	List<SocialImageFile> findAllBySocial(Social socialBoard);

	void deleteAllBySocial(Social socialBoard);

	@Query("SELECT sif FROM SocialImageFile sif WHERE sif.social.id IN :socialIds AND sif.deletedAt IS NULL")
	List<SocialImageFile> findAllBySocialIdIn(@Param("socialIds") List<Long> socialIds);
}
