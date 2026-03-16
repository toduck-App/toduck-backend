package im.toduck.domain.badge.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import im.toduck.domain.badge.persistence.entity.Badge;
import im.toduck.domain.badge.persistence.entity.BadgeCode;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
	Optional<Badge> findByCode(BadgeCode code);
}
