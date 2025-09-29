package im.toduck.domain.backoffice.common.mapper;

import java.util.List;
import java.util.Map;

import im.toduck.domain.backoffice.persistence.entity.AppVersion;
import im.toduck.domain.backoffice.persistence.entity.AppVersionPlatform;
import im.toduck.domain.backoffice.presentation.dto.response.AppVersionListResponse;
import im.toduck.domain.backoffice.presentation.dto.response.AppVersionResponse;
import im.toduck.domain.backoffice.presentation.dto.response.UpdatePolicyItemResponse;
import im.toduck.domain.backoffice.presentation.dto.response.UpdatePolicyResponse;
import im.toduck.domain.backoffice.presentation.dto.response.VersionCheckResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppVersionMapper {

	public static VersionCheckResponse toVersionCheckResponse(
		final String updateStatus,
		final String latestVersion
	) {
		return VersionCheckResponse.builder()
			.updateStatus(updateStatus)
			.latestVersion(latestVersion)
			.build();
	}

	public static AppVersionResponse toAppVersionResponse(final AppVersion appVersion) {
		return AppVersionResponse.builder()
			.id(appVersion.getId())
			.platform(appVersion.getPlatform())
			.version(appVersion.getVersion())
			.releaseDate(appVersion.getReleaseDate())
			.updateType(appVersion.getUpdateType())
			.updateTypeDescription(appVersion.getUpdateType().description())
			.canDelete(appVersion.canDelete())
			.createdAt(appVersion.getCreatedAt())
			.build();
	}

	public static AppVersionListResponse toAppVersionListResponse(
		final Map<AppVersionPlatform, List<AppVersion>> versionsByPlatform
	) {
		List<AppVersionResponse> iosVersions = versionsByPlatform.get(AppVersionPlatform.IOS)
			.stream()
			.map(AppVersionMapper::toAppVersionResponse)
			.toList();

		List<AppVersionResponse> androidVersions = versionsByPlatform.get(AppVersionPlatform.ANDROID)
			.stream()
			.map(AppVersionMapper::toAppVersionResponse)
			.toList();

		return AppVersionListResponse.builder()
			.ios(iosVersions)
			.android(androidVersions)
			.build();
	}

	public static UpdatePolicyResponse toUpdatePolicyResponse(
		final Map<AppVersionPlatform, List<AppVersion>> versionsByPlatform
	) {
		List<UpdatePolicyItemResponse> iosVersions = versionsByPlatform.get(AppVersionPlatform.IOS)
			.stream()
			.map(AppVersionMapper::toUpdatePolicyItemResponse)
			.toList();

		List<UpdatePolicyItemResponse> androidVersions = versionsByPlatform.get(AppVersionPlatform.ANDROID)
			.stream()
			.map(AppVersionMapper::toUpdatePolicyItemResponse)
			.toList();

		return UpdatePolicyResponse.builder()
			.ios(iosVersions)
			.android(androidVersions)
			.build();
	}

	private static UpdatePolicyItemResponse toUpdatePolicyItemResponse(final AppVersion appVersion) {
		return UpdatePolicyItemResponse.builder()
			.id(appVersion.getId())
			.version(appVersion.getVersion())
			.updateType(appVersion.getUpdateType())
			.build();
	}
}
