package im.toduck.domain.events.detail.common.mapper;

import im.toduck.domain.events.detail.persistence.entity.EventsDetail;
import im.toduck.domain.events.detail.persistence.entity.EventsDetailImg;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventsDetailImgMapper {
	public static EventsDetailImg toEventDetailImg(EventsDetail eventsDetail, String imgUrls) {
		return EventsDetailImg.builder()
			.eventsDetail(eventsDetail)
			.detailImgUrl(imgUrls)
			.build();
	}

	public static String fromEventDetailImg(EventsDetailImg eventsDetailImg) {
		return eventsDetailImg.getDetailImgUrl();
	}
}
