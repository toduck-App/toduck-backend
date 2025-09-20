package im.toduck.domain.backoffice.persistence.entity;

import java.time.LocalDate;

import im.toduck.global.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "app_version")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AppVersion extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AppVersionPlatform platform;

	@Column(nullable = false, length = 20)
	private String version;

	@Column(name = "release_date", nullable = false)
	private LocalDate releaseDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "update_type", nullable = false)
	private UpdateType updateType;

	@Builder
	private AppVersion(
		final AppVersionPlatform platform,
		final String version,
		final LocalDate releaseDate,
		final UpdateType updateType
	) {
		this.platform = platform;
		this.version = version;
		this.releaseDate = releaseDate;
		this.updateType = updateType;
	}

	public void updateType(final UpdateType updateType) {
		this.updateType = updateType;
	}

	public boolean canDelete() {
		return updateType == UpdateType.NONE;
	}
}
