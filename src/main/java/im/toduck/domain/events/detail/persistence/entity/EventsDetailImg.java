package im.toduck.domain.events.detail.persistence.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import im.toduck.global.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "events_detail_img")
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE events_detail_img SET deleted_at = NOW() where id=?")
@SQLRestriction(value = "deleted_at is NULL")
public class EventsDetailImg extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "events_detail_id", nullable = false)
	private EventsDetail eventsDetail;

	@Column(name = "detail_img_url", length = 1023, nullable = false)
	private String detailImgUrl;

	@Builder
	private EventsDetailImg(final EventsDetail eventsDetail, final String detailImgUrl) {
		this.eventsDetail = eventsDetail;
		this.detailImgUrl = detailImgUrl;
	}

	public void updateEventsDetail(final EventsDetail eventsDetail) {
		this.eventsDetail = eventsDetail;
	}

	public void updateDetailImgUrl(final String detailImgUrl) {
		this.detailImgUrl = detailImgUrl;
	}
}
