package im.toduck.domain.social.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import im.toduck.domain.social.persistence.entity.Like;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.user.persistence.entity.User;

public interface LikeRepository extends JpaRepository<Like, Long> {
	List<Like> findAllBySocial(Social socialBoard);

	Optional<Like> findByUserAndSocial(User user, Social socialBoard);
}
