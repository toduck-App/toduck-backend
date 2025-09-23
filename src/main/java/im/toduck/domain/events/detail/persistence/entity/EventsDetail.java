package im.toduck.domain.events.detail.persistence.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import im.toduck.domain.events.events.persistence.entity.Events;
import im.toduck.global.base.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "events_detail")
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE events_detail SET deleted_at = NOW() where id=?")
@SQLRestriction(value = "deleted_at is NULL")
public class EventsDetail extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "events_id", nullable = false, unique = true)
	private Events events;

	@Column(name = "routing_url", length = 1023, nullable = true)
	private String routingUrl;

	@OneToMany(mappedBy = "eventsDetail", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<EventsDetailImg> eventsDetailImgs = new ArrayList<>();

	@Builder
	private EventsDetail(Events events, String routingUrl) {
		this.events = events;
		this.routingUrl = routingUrl;
	}

	public void updateEvents(final Events events) {
		this.events = events;
	}

	public void updateRoutingUrl(final String routingUrl) {
		this.routingUrl = routingUrl;
	}
}
