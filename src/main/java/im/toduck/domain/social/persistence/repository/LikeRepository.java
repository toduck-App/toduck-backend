package im.toduck.domain.social.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import im.toduck.domain.social.persistence.entity.Like;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.user.persistence.entity.User;

public interface LikeRepository extends JpaRepository<Like, Long> {
	List<Like> findAllBySocial(Social socialBoard);

	Optional<Like> findByUserAndSocial(User user, Social socialBoard);

	@Query("SELECT l.social.id FROM Like l "
		+ "WHERE l.user = :user AND l.social.id IN :socialIds AND l.deletedAt IS NULL")
	List<Long> findSocialIdsByUserAndSocialIdIn(@Param("user") User user, @Param("socialIds") List<Long> socialIds);
}
