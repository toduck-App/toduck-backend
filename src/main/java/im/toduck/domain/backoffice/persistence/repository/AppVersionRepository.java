package im.toduck.domain.backoffice.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import im.toduck.domain.backoffice.persistence.entity.AppVersion;
import im.toduck.domain.backoffice.persistence.entity.AppVersionPlatform;
import im.toduck.domain.backoffice.persistence.entity.UpdateType;

@Repository
public interface AppVersionRepository extends JpaRepository<AppVersion, Long> {

	List<AppVersion> findByPlatformOrderByReleaseDateDesc(final AppVersionPlatform platform);

	Optional<AppVersion> findByPlatformAndVersion(final AppVersionPlatform platform, final String version);

	Optional<AppVersion> findByPlatformAndUpdateType(
		final AppVersionPlatform platform,
		final UpdateType updateType
	);

	boolean existsByPlatformAndVersion(final AppVersionPlatform platform, final String version);
}

