package im.toduck.domain.backoffice.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.backoffice.domain.usecase.AppVersionUseCase;
import im.toduck.domain.backoffice.presentation.api.AppVersionApi;
import im.toduck.domain.backoffice.presentation.dto.request.AppVersionCreateRequest;
import im.toduck.domain.backoffice.presentation.dto.request.UpdatePolicyRequest;
import im.toduck.domain.backoffice.presentation.dto.response.AppVersionListResponse;
import im.toduck.domain.backoffice.presentation.dto.response.AppVersionResponse;
import im.toduck.domain.backoffice.presentation.dto.response.UpdatePolicyResponse;
import im.toduck.domain.backoffice.presentation.dto.response.VersionCheckResponse;
import im.toduck.global.presentation.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AppVersionController implements AppVersionApi {

	private final AppVersionUseCase appVersionUseCase;

	@Override
	@PreAuthorize("true")
	@GetMapping("/v1/backoffice/app/version-check")
	public ResponseEntity<ApiResponse<VersionCheckResponse>> checkVersion(
		@RequestParam final String platform,
		@RequestParam final String version
	) {
		VersionCheckResponse response = appVersionUseCase.checkVersion(platform, version);
		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}

	@GetMapping("/v1/backoffice/app/versions")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<AppVersionListResponse>> getAllVersions() {
		AppVersionListResponse response = appVersionUseCase.getAllVersions();
		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}

	@PostMapping("/v1/backoffice/app/versions")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<AppVersionResponse>> createVersion(
		@RequestBody @Valid final AppVersionCreateRequest request
	) {
		AppVersionResponse response = appVersionUseCase.createVersion(request);
		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}

	@DeleteMapping("/v1/backoffice/app/versions/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<?>> deleteVersion(@PathVariable final Long id) {
		appVersionUseCase.deleteVersion(id);
		return ResponseEntity.ok(ApiResponse.createSuccessWithNoContent());
	}

	@GetMapping("/v1/backoffice/app/update-policies")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<UpdatePolicyResponse>> getUpdatePolicies() {
		UpdatePolicyResponse response = appVersionUseCase.getUpdatePolicies();
		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}

	@PutMapping("/v1/backoffice/app/update-policies")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<?>> updatePolicies(
		@RequestBody @Valid final UpdatePolicyRequest request
	) {
		appVersionUseCase.updatePolicies(request);
		return ResponseEntity.ok(ApiResponse.createSuccessWithNoContent());
	}
}
