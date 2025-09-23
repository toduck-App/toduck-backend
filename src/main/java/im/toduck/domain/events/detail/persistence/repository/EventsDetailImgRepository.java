package im.toduck.domain.events.detail.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import im.toduck.domain.events.detail.persistence.entity.EventsDetail;
import im.toduck.domain.events.detail.persistence.entity.EventsDetailImg;

public interface EventsDetailImgRepository extends JpaRepository<EventsDetailImg, Long> {
	List<EventsDetailImg> findAllByOrderByIdAsc();

	void deleteAllByEventsDetail(EventsDetail eventsDetail);
}
