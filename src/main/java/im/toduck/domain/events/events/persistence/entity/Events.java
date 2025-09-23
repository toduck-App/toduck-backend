package im.toduck.domain.events.events.persistence.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import im.toduck.global.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "events")
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE events SET deleted_at = NOW() where id=?")
@SQLRestriction(value = "deleted_at is NULL")
public class Events extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 63, nullable = false)
	private String eventName;

	@Column(nullable = false)
	private LocalDateTime startAt;

	@Column(nullable = false)
	private LocalDateTime endAt;

	@Column(length = 1023, nullable = false)
	private String thumbUrl;

	@Column(length = 63, nullable = false)
	private String appVersion;

	@Builder
	private Events(String eventName,
		LocalDateTime startAt,
		LocalDateTime endAt,
		String thumbUrl,
		String appVersion) {
		this.eventName = eventName;
		this.startAt = startAt;
		this.endAt = endAt;
		this.thumbUrl = thumbUrl;
		this.appVersion = appVersion;
	}

	public void updateEventName(final String eventName) {
		this.eventName = eventName;
	}

	public void updateStartAt(final LocalDateTime startAt) {
		this.startAt = startAt;
	}

	public void updateEndAt(final LocalDateTime endAt) {
		this.endAt = endAt;
	}

	public void updateThumbUrl(final String thumbUrl) {
		this.thumbUrl = thumbUrl;
	}

	public void updateAppVersion(final String appVersion) {
		this.appVersion = appVersion;
	}
}
