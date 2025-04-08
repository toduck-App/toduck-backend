package im.toduck.domain.user.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import im.toduck.domain.user.persistence.entity.Follow;
import im.toduck.domain.user.persistence.entity.User;

public interface FollowRepository extends JpaRepository<Follow, Long> {
	boolean existsByFollowerAndFollowed(User follower, User followed);

	Optional<Follow> findByFollowerAndFollowed(User follower, User followed);

	long countByFollower_Id(Long followerId);

	long countByFollowed_Id(Long followedId);
}
