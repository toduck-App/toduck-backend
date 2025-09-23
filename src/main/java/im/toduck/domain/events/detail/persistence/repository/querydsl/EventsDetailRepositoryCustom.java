package im.toduck.domain.events.detail.persistence.repository.querydsl;

import java.util.List;

import im.toduck.domain.events.detail.persistence.entity.EventsDetail;

public interface EventsDetailRepositoryCustom {
	List<EventsDetail> findAllWithImgs();
}
