package im.toduck.domain.social.persistence.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import im.toduck.domain.social.persistence.entity.Social;
import io.lettuce.core.dynamic.annotation.Param;

public interface SocialRepository extends JpaRepository<Social, Long> {
	@Query("SELECT s FROM Social s WHERE s.id > :after ORDER BY s.id DESC")
	List<Social> findByIdAfterOrderByIdDesc(@Param("after") Long after, Pageable pageable);

	@Query("SELECT s FROM Social s WHERE s.deletedAt IS NULL ORDER BY s.id DESC")
	List<Social> findLatestSocials(Pageable pageable);
}
