package im.toduck.domain.backoffice.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.backoffice.domain.usecase.NotificationStatisticsUseCase;
import im.toduck.domain.backoffice.domain.usecase.UserManagementUseCase;
import im.toduck.domain.backoffice.presentation.api.UserManagementApi;
import im.toduck.domain.backoffice.presentation.dto.request.UserSearchRequest;
import im.toduck.domain.backoffice.presentation.dto.request.UserSuspendRequest;
import im.toduck.domain.backoffice.presentation.dto.response.AccountDeletionLogListResponse;
import im.toduck.domain.backoffice.presentation.dto.response.DeletionReasonStatisticsResponse;
import im.toduck.domain.backoffice.presentation.dto.response.NotificationStatisticsResponse;
import im.toduck.domain.backoffice.presentation.dto.response.UserListPaginationResponse;
import im.toduck.domain.backoffice.presentation.dto.response.UserStatisticsResponse;
import im.toduck.domain.user.persistence.entity.UserRole;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/backoffice/users")
@RequiredArgsConstructor
public class UserManagementController implements UserManagementApi {

	private final UserManagementUseCase userManagementUseCase;
	private final NotificationStatisticsUseCase notificationStatisticsUseCase;

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
	@GetMapping("/statistics")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<UserStatisticsResponse>> getUserStatistics() {
		UserStatisticsResponse response = userManagementUseCase.getUserStatistics();
		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}

	@Override
	@GetMapping("/notifications/statistics")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<NotificationStatisticsResponse>> getNotificationStatistics() {
		NotificationStatisticsResponse response = notificationStatisticsUseCase.getNotificationStatistics();
		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}

	@Override
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<UserListPaginationResponse>> getUsers(
		@RequestParam(required = false) final String keyword,
		@RequestParam(required = false) final String searchType,
		@RequestParam(defaultValue = "all") final String status,
		@RequestParam(required = false) final UserRole role,
		@RequestParam(required = false) final String provider,
		@RequestParam(defaultValue = "createdAt") final String sortBy,
		@RequestParam(defaultValue = "desc") final String sortDirection,
		@RequestParam(defaultValue = "0") final Integer page,
		@RequestParam(defaultValue = "20") final Integer size
	) {
		UserSearchRequest searchRequest = new UserSearchRequest(
			keyword, searchType, status, role, provider, sortBy, sortDirection, page, size
		);

		UserListPaginationResponse response = userManagementUseCase.getUsersWithFilters(searchRequest);
		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}

	@Override
	@PostMapping("/{userId}/suspend")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<?>> suspendUser(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@PathVariable final Long userId,
		@RequestBody @Valid final UserSuspendRequest request) {
		userManagementUseCase.suspendUser(userDetails.getUserId(), userId, request);
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
