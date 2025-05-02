package im.toduck.domain.user.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import im.toduck.domain.user.persistence.entity.Follow;
import im.toduck.domain.user.persistence.entity.User;

public interface FollowRepository extends JpaRepository<Follow, Long> {
	boolean existsByFollowerAndFollowed(User follower, User followed);

	Optional<Follow> findByFollowerAndFollowed(User follower, User followed);

	long countByFollower_Id(Long followerId);

	long countByFollowed_Id(Long followedId);

	@Modifying(clearAutomatically = true)
	@Query("DELETE FROM Follow f WHERE f.follower = :user OR f.followed = :user")
	void deleteAllByUser(@Param("user") User user);
}
