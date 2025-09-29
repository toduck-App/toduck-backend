package im.toduck.domain.backoffice.presentation.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.backoffice.domain.usecase.BroadcastNotificationUseCase;
import im.toduck.domain.backoffice.presentation.api.BroadcastNotificationApi;
import im.toduck.domain.backoffice.presentation.dto.request.BroadcastNotificationCreateRequest;
import im.toduck.domain.backoffice.presentation.dto.response.BroadcastNotificationListResponse;
import im.toduck.domain.backoffice.presentation.dto.response.BroadcastNotificationResponse;
import im.toduck.global.presentation.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/backoffice/broadcast-notifications")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.quartz.auto-startup", havingValue = "true", matchIfMissing = true)
public class BroadcastNotificationController implements BroadcastNotificationApi {

	private final BroadcastNotificationUseCase broadcastNotificationUseCase;

	@Override
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<BroadcastNotificationResponse>> createBroadcastNotification(
		@RequestBody @Valid final BroadcastNotificationCreateRequest request
	) {
		BroadcastNotificationResponse response = broadcastNotificationUseCase.createBroadcastNotification(request);
		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}

	@Override
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<BroadcastNotificationListResponse>> getBroadcastNotifications() {
		BroadcastNotificationListResponse response = broadcastNotificationUseCase.getAllBroadcastNotifications();
		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}

	@Override
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<?>> cancelBroadcastNotification(@PathVariable final Long id) {
		broadcastNotificationUseCase.cancelBroadcastNotification(id);
		return ResponseEntity.ok(ApiResponse.createSuccessWithNoContent());
	}
}
