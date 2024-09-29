package im.toduck.domain.user.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import im.toduck.domain.user.persistence.entity.Block;
import im.toduck.domain.user.persistence.entity.User;

public interface BlockRepository extends JpaRepository<Block, Long> {

	boolean existsByBlockerAndBlocked(User blocker, User blocked);

	Optional<Block> findByBlockerAndBlocked(User blocker, User blocked);

	boolean existsByBlockerIdAndBlockedId(Long blockerId, Long blockedId);
}
