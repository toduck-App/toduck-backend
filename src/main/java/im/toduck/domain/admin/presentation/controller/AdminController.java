package im.toduck.domain.admin.presentation.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.admin.domain.usecase.AdminUseCase;
import im.toduck.domain.admin.presentation.api.AdminApi;
import im.toduck.domain.admin.presentation.dto.request.AdminCreateRequest;
import im.toduck.domain.admin.presentation.dto.request.AdminUpdateRequest;
import im.toduck.domain.admin.presentation.dto.response.AdminListResponse;
import im.toduck.domain.admin.presentation.dto.response.AdminResponse;
import im.toduck.global.presentation.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin")
public class AdminController implements AdminApi {

	private final AdminUseCase adminUseCase;

	@Override
	@GetMapping("/{userId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<AdminResponse>> getAdmin(
		@PathVariable final Long userId
	) {
		AdminResponse response = adminUseCase.getAdmin(userId);

		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}

	@Override
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<AdminListResponse>> getAdmins() {
		AdminListResponse response = adminUseCase.getAdmins();

		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}

	@Override
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<Map<String, Object>>> createAdmin(
		@RequestBody @Valid final AdminCreateRequest request
	) {
		adminUseCase.createAdmin(request);

		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@PatchMapping("/{userId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<Map<String, Object>>> updateAdmin(
		@PathVariable final Long userId,
		@RequestBody @Valid final AdminUpdateRequest request
	) {
		adminUseCase.updateAdmin(userId, request);

		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@DeleteMapping("/{userId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<Map<String, Object>>> deleteAdmin(
		@PathVariable final Long userId
	) {
		adminUseCase.deleteAdmin(userId);

		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}
}
