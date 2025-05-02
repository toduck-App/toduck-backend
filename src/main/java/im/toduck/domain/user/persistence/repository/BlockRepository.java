package im.toduck.domain.user.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import im.toduck.domain.user.persistence.entity.Block;
import im.toduck.domain.user.persistence.entity.User;

public interface BlockRepository extends JpaRepository<Block, Long> {

	boolean existsByBlockerAndBlocked(User blocker, User blocked);

	Optional<Block> findByBlockerAndBlocked(User blocker, User blocked);

	@Modifying(clearAutomatically = true)
	@Query("DELETE FROM Block b WHERE b.blocked = :user OR b.blocker = :user")
	void deleteAllByUser(@Param("user") User user);
}
