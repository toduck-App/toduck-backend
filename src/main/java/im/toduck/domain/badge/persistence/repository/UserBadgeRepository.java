package im.toduck.domain.badge.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import im.toduck.domain.badge.persistence.entity.Badge;
import im.toduck.domain.badge.persistence.entity.UserBadge;
import im.toduck.domain.user.persistence.entity.User;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
	boolean existsByUserAndBadge(User user, Badge badge);

	List<UserBadge> findAllByUser(User user);

	List<UserBadge> findAllByUserAndIsSeenFalse(User user);

	Optional<UserBadge> findByUserAndIsRepresentativeTrue(User user);

	Optional<UserBadge> findByUserAndBadge(User user, Badge badge);
}
