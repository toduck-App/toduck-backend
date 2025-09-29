package im.toduck.domain.backoffice.domain.service;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.backoffice.persistence.entity.AppVersion;
import im.toduck.domain.backoffice.persistence.entity.AppVersionPlatform;
import im.toduck.domain.backoffice.persistence.entity.UpdateType;
import im.toduck.domain.backoffice.persistence.repository.AppVersionRepository;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppVersionService {

	private static final Pattern VERSION_PATTERN = Pattern.compile("^\\d+\\.\\d+\\.\\d+$");

	private final AppVersionRepository appVersionRepository;

	@Transactional(readOnly = true)
	public List<AppVersion> getVersionsByPlatform(final AppVersionPlatform platform) {
		return appVersionRepository.findByPlatformOrderByReleaseDateDesc(platform);
	}

	@Transactional(readOnly = true)
	public Map<AppVersionPlatform, List<AppVersion>> getAllVersionsByPlatform() {
		Map<AppVersionPlatform, List<AppVersion>> versionsByPlatform = new EnumMap<>(AppVersionPlatform.class);

		for (AppVersionPlatform platform : AppVersionPlatform.values()) {
			versionsByPlatform.put(platform, getVersionsByPlatform(platform));
		}

		return versionsByPlatform;
	}

	@Transactional(readOnly = true)
	public Optional<AppVersion> getVersionById(final Long id) {
		return appVersionRepository.findById(id);
	}

	@Transactional
	public AppVersion createVersion(
		final AppVersionPlatform platform,
		final String version,
		final LocalDate releaseDate
	) {
		validateVersionFormat(version);

		if (appVersionRepository.existsByPlatformAndVersion(platform, version)) {
			throw CommonException.from(ExceptionCode.DUPLICATE_APP_VERSION);
		}

		AppVersion appVersion = AppVersion.builder()
			.platform(platform)
			.version(version)
			.releaseDate(releaseDate)
			.updateType(UpdateType.NONE)
			.build();

		return appVersionRepository.save(appVersion);
	}

	@Transactional
	public void deleteVersion(final Long id) {
		AppVersion version = appVersionRepository.findById(id)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_APP_VERSION));

		if (!version.canDelete()) {
			throw CommonException.from(ExceptionCode.CANNOT_DELETE_APP_VERSION);
		}

		appVersionRepository.delete(version);
	}

	@Transactional
	public void updateVersionType(final Long id, final UpdateType updateType) {
		AppVersion version = appVersionRepository.findById(id)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_APP_VERSION));

		version.updateType(updateType);
		appVersionRepository.save(version);
	}

	@Transactional(readOnly = true)
	public String determineUpdateStatus(final AppVersionPlatform platform, final String currentVersion) {
		validateVersionFormat(currentVersion);

		Optional<AppVersion> version = appVersionRepository.findByPlatformAndVersion(platform, currentVersion);

		if (version.isPresent()) {
			UpdateType updateType = version.get().getUpdateType();
			return updateType == UpdateType.LATEST ? "NONE" : updateType.name();
		}

		return "NONE";
	}

	@Transactional(readOnly = true)
	public String getLatestVersion(final AppVersionPlatform platform) {
		Optional<AppVersion> latestVersion = appVersionRepository.findByPlatformAndUpdateType(platform,
			UpdateType.LATEST);
		return latestVersion.map(AppVersion::getVersion).orElse(null);
	}

	public void validateLatestVersionPolicy(final AppVersionPlatform platform, final List<Long> latestVersionIds) {
		if (latestVersionIds.size() > 1) {
			throw CommonException.from(ExceptionCode.DUPLICATE_LATEST_VERSION);
		}
	}

	private void validateVersionFormat(final String version) {
		if (version == null || !VERSION_PATTERN.matcher(version).matches()) {
			throw CommonException.from(ExceptionCode.INVALID_VERSION_FORMAT);
		}
	}
}
