package im.toduck.domain.backoffice.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.backoffice.domain.usecase.UserManagementUseCase;
import im.toduck.domain.backoffice.presentation.api.UserManagementApi;
import im.toduck.domain.backoffice.presentation.dto.request.UserSuspendRequest;
import im.toduck.domain.backoffice.presentation.dto.response.AccountDeletionLogListResponse;
import im.toduck.domain.backoffice.presentation.dto.response.DeletionReasonStatisticsResponse;
import im.toduck.domain.backoffice.presentation.dto.response.UserDetailResponse;
import im.toduck.domain.backoffice.presentation.dto.response.UserListResponse;
import im.toduck.global.presentation.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/backoffice/users")
@RequiredArgsConstructor
public class UserManagementController implements UserManagementApi {

	private final UserManagementUseCase userManagementUseCase;

	@Override
	@GetMapping("/deletion-logs")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<AccountDeletionLogListResponse>> getDeletionLogs() {
		AccountDeletionLogListResponse response = userManagementUseCase.getAllDeletionLogs();
		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}

	@Override
	@GetMapping("/deletion-logs/statistics")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<DeletionReasonStatisticsResponse>> getDeletionReasonStatistics() {
		DeletionReasonStatisticsResponse response = userManagementUseCase.getDeletionReasonStatistics();
		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}

	@Override
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<UserListResponse>> getUsers() {
		UserListResponse response = userManagementUseCase.getAllUsers();
		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}

	@Override
	@GetMapping("/{userId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<UserDetailResponse>> getUserDetail(@PathVariable final Long userId) {
		UserDetailResponse response = userManagementUseCase.getUserDetail(userId);
		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}

	@Override
	@PostMapping("/{userId}/suspend")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<?>> suspendUser(@PathVariable final Long userId,
			@RequestBody @Valid final UserSuspendRequest request) {
		userManagementUseCase.suspendUser(userId, request);
		return ResponseEntity.ok(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@PostMapping("/{userId}/unsuspend")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<?>> unsuspendUser(@PathVariable final Long userId) {
		userManagementUseCase.unsuspendUser(userId);
		return ResponseEntity.ok(ApiResponse.createSuccessWithNoContent());
	}
}
