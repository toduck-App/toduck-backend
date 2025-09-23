package im.toduck.domain.events.detail.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import im.toduck.domain.events.detail.persistence.entity.EventsDetail;

@Repository
public interface EventsDetailRepository extends JpaRepository<EventsDetail, Long> {
	List<EventsDetail> findAllByOrderByIdAsc();

	boolean existsByEventsId(Long eventsId);
}
