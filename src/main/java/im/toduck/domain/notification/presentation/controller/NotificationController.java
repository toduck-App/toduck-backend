package im.toduck.domain.notification.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.notification.domain.usecase.NotificationUseCase;
import im.toduck.domain.notification.presentation.api.NotificationApi;
import im.toduck.domain.notification.presentation.dto.request.DeviceTokenRegisterRequest;
import im.toduck.domain.notification.presentation.dto.request.NotificationSettingUpdateRequest;
import im.toduck.domain.notification.presentation.dto.response.NotificationListResponse;
import im.toduck.domain.notification.presentation.dto.response.NotificationSettingResponse;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/notifications")
public class NotificationController implements NotificationApi {

	private final NotificationUseCase notificationUseCase;

	@Override
	@PostMapping("/device-tokens")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<?>> registerDeviceToken(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody @Valid DeviceTokenRegisterRequest request
	) {
		notificationUseCase.registerDeviceToken(
			userDetails.getUserId(),
			request.token(),
			request.deviceType()
		);

		return ResponseEntity.ok(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@DeleteMapping("/device-tokens/{token}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<?>> removeDeviceToken(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable String token
	) {
		notificationUseCase.removeDeviceToken(userDetails.getUserId(), token);

		return ResponseEntity.ok(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@GetMapping("/settings")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<NotificationSettingResponse>> getNotificationSettings(
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		NotificationSettingResponse response = notificationUseCase.getNotificationSettings(userDetails.getUserId());

		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}

	@Override
	@PutMapping("/settings")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<NotificationSettingResponse>> updateNotificationSettings(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody @Valid NotificationSettingUpdateRequest request
	) {
		NotificationSettingResponse response = notificationUseCase.updateNotificationSettings(
			userDetails.getUserId(),
			request
		);

		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}

	@Override
	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<NotificationListResponse>> getNotifications(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "20") int size
	) {
		NotificationListResponse response = notificationUseCase.getNotifications(
			userDetails.getUserId(),
			page,
			size
		);

		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}

	@Override
	@PatchMapping("/{notificationId}/read")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<?>> markNotificationAsRead(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable Long notificationId
	) {
		notificationUseCase.markNotificationAsRead(userDetails.getUserId(), notificationId);

		return ResponseEntity.ok(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@PatchMapping("/read-all")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<?>> markAllNotificationsAsRead(
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		notificationUseCase.markAllNotificationsAsRead(userDetails.getUserId());

		return ResponseEntity.ok(ApiResponse.createSuccessWithNoContent());
	}
}
