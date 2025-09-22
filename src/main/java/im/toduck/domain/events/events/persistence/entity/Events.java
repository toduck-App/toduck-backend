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
import lombok.Setter;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE diary SET deleted_at = NOW() where id=?")
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
}
