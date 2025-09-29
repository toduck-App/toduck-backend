package im.toduck.domain.events.detail.persistence.repository.querydsl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import im.toduck.domain.events.detail.persistence.entity.EventsDetail;
import im.toduck.domain.events.detail.persistence.entity.QEventsDetail;
import im.toduck.domain.events.detail.persistence.entity.QEventsDetailImg;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EventsDetailRepositoryCustomImpl implements EventsDetailRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<EventsDetail> findAllWithImgs() {
		QEventsDetail ed = QEventsDetail.eventsDetail;
		QEventsDetailImg edi = QEventsDetailImg.eventsDetailImg;

		return queryFactory
			.selectFrom(ed)
			.leftJoin(ed.eventsDetailImgs, edi).fetchJoin()
			.orderBy(ed.id.asc())
			.fetch();
	}
}
