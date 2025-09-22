package im.toduck.domain.events.events.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import im.toduck.domain.events.events.persistence.entity.Events;

@Repository
public interface EventsRepository extends JpaRepository<Events, Long> {

	List<Events> findAllByOrderByIdAsc();
}
