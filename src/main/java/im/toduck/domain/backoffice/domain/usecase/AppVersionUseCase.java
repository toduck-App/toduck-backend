package im.toduck.domain.backoffice.domain.usecase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.backoffice.common.mapper.AppVersionMapper;
import im.toduck.domain.backoffice.domain.service.AppVersionService;
import im.toduck.domain.backoffice.persistence.entity.AppVersion;
import im.toduck.domain.backoffice.persistence.entity.AppVersionPlatform;
import im.toduck.domain.backoffice.persistence.entity.UpdateType;
import im.toduck.domain.backoffice.presentation.dto.request.AppVersionCreateRequest;
import im.toduck.domain.backoffice.presentation.dto.request.UpdatePolicyItemRequest;
import im.toduck.domain.backoffice.presentation.dto.request.UpdatePolicyRequest;
import im.toduck.domain.backoffice.presentation.dto.response.AppVersionListResponse;
import im.toduck.domain.backoffice.presentation.dto.response.AppVersionResponse;
import im.toduck.domain.backoffice.presentation.dto.response.UpdatePolicyResponse;
import im.toduck.domain.backoffice.presentation.dto.response.VersionCheckResponse;
import im.toduck.global.annotation.UseCase;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class AppVersionUseCase {

	private final AppVersionService appVersionService;

	@Transactional(readOnly = true)
	public VersionCheckResponse checkVersion(final String platform, final String version) {
		AppVersionPlatform platformEnum;
		try {
			platformEnum = AppVersionPlatform.valueOf(platform.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw CommonException.from(ExceptionCode.INVALID_VERSION_FORMAT);
		}

		String updateStatus = appVersionService.determineUpdateStatus(platformEnum, version);
		String latestVersion = appVersionService.getLatestVersion(platformEnum);

		log.info("앱 버전 체크 - Platform: {}, CurrentVersion: {}, UpdateStatus: {}, LatestVersion: {}",
			platform, version, updateStatus, latestVersion);

		return AppVersionMapper.toVersionCheckResponse(updateStatus, latestVersion);
	}

	@Transactional(readOnly = true)
	public AppVersionListResponse getAllVersions() {
		Map<AppVersionPlatform, List<AppVersion>> versionsByPlatform = appVersionService.getAllVersionsByPlatform();

		log.info("백오피스 앱 버전 목록 조회 - iOS: {}개, Android: {}개",
			versionsByPlatform.get(AppVersionPlatform.IOS).size(),
			versionsByPlatform.get(AppVersionPlatform.ANDROID).size());

		return AppVersionMapper.toAppVersionListResponse(versionsByPlatform);
	}

	@Transactional
	public AppVersionResponse createVersion(final AppVersionCreateRequest request) {
		AppVersion version = appVersionService.createVersion(
			request.platform(),
			request.version(),
			request.releaseDate()
		);

		log.info("앱 버전 생성 - Platform: {}, Version: {}, ReleaseDate: {}",
			request.platform(), request.version(), request.releaseDate());

		return AppVersionMapper.toAppVersionResponse(version);
	}

	@Transactional
	public void deleteVersion(final Long id) {
		AppVersion version = appVersionService.getVersionById(id)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_APP_VERSION));

		appVersionService.deleteVersion(id);

		log.info("앱 버전 삭제 - VersionId: {}, Platform: {}, Version: {}",
			id, version.getPlatform(), version.getVersion());
	}

	@Transactional(readOnly = true)
	public UpdatePolicyResponse getUpdatePolicies() {
		Map<AppVersionPlatform, List<AppVersion>> policiesByPlatform = appVersionService.getAllVersionsByPlatform();

		log.info("백오피스 업데이트 정책 조회");

		return AppVersionMapper.toUpdatePolicyResponse(policiesByPlatform);
	}

	@Transactional
	public void updatePolicies(final UpdatePolicyRequest request) {
		List<UpdatePolicyItemRequest> allPolicies = new ArrayList<>();
		allPolicies.addAll(request.ios());
		allPolicies.addAll(request.android());

		validateLatestVersionPolicies(request);
		validateVersionIds(allPolicies);

		for (UpdatePolicyItemRequest policy : allPolicies) {
			appVersionService.updateVersionType(policy.id(), policy.updateType());
		}

		log.info("업데이트 정책 수정 - 총 {}개 버전 정책 업데이트", allPolicies.size());
	}

	private void validateLatestVersionPolicies(final UpdatePolicyRequest request) {
		List<Long> iosLatestVersionIds = request.ios().stream()
			.filter(policy -> policy.updateType() == UpdateType.LATEST)
			.map(UpdatePolicyItemRequest::id)
			.toList();

		appVersionService.validateLatestVersionPolicy(AppVersionPlatform.IOS, iosLatestVersionIds);

		List<Long> androidLatestVersionIds = request.android().stream()
			.filter(policy -> policy.updateType() == UpdateType.LATEST)
			.map(UpdatePolicyItemRequest::id)
			.toList();

		appVersionService.validateLatestVersionPolicy(AppVersionPlatform.ANDROID, androidLatestVersionIds);
	}

	private void validateVersionIds(final List<UpdatePolicyItemRequest> policies) {
		for (UpdatePolicyItemRequest policy : policies) {
			if (appVersionService.getVersionById(policy.id()).isEmpty()) {
				throw CommonException.from(ExceptionCode.NOT_FOUND_APP_VERSION);
			}
		}
	}
}
