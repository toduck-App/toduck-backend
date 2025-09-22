package im.toduck.domain.events.social.persistence.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import im.toduck.domain.events.social.persistence.entity.EventsSocial;

@Repository
public interface EventsSocialRepository extends JpaRepository<EventsSocial, Long> {
	Optional<Object> findByDateAndUserId(LocalDate date, Long userId);

	List<EventsSocial> findAllByOrderByIdAsc();
}
