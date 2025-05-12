package im.toduck.domain.notification.presentation.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import im.toduck.domain.notification.presentation.dto.request.DeviceTokenRegisterRequest;
import im.toduck.domain.notification.presentation.dto.request.NotificationSettingUpdateRequest;
import im.toduck.domain.notification.presentation.dto.response.NotificationListResponse;
import im.toduck.domain.notification.presentation.dto.response.NotificationSettingResponse;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.swagger.ApiSuccessResponseExplanation;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Notification")
public interface NotificationApi {
	@Operation(
		summary = "디바이스 토큰 등록",
		description = "푸시 알림을 위한 디바이스 토큰을 등록합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "디바이스 토큰 등록 성공"
		)
	)
	ResponseEntity<ApiResponse<?>> registerDeviceToken(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@RequestBody @Valid final DeviceTokenRegisterRequest request
	);

	@Operation(
		summary = "디바이스 토큰 삭제",
		description = "등록된 디바이스 토큰을 삭제합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "디바이스 토큰 삭제 성공"
		)
	)
	ResponseEntity<ApiResponse<?>> removeDeviceToken(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@PathVariable final String token
	);

	@Operation(
		summary = "알림 설정 조회",
		description = "사용자의 알림 설정을 조회합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = NotificationSettingResponse.class,
			description = "알림 설정 조회 성공"
		)
	)
	ResponseEntity<ApiResponse<NotificationSettingResponse>> getNotificationSettings(
		@AuthenticationPrincipal final CustomUserDetails userDetails
	);

	@Operation(
		summary = "알림 설정 업데이트",
		description = "사용자의 알림 설정을 업데이트합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = NotificationSettingResponse.class,
			description = "알림 설정 업데이트 성공"
		)
	)
	ResponseEntity<ApiResponse<NotificationSettingResponse>> updateNotificationSettings(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@RequestBody @Valid final NotificationSettingUpdateRequest request
	);

	@Operation(
		summary = "알림 목록 조회",
		description = "사용자의 알림 목록을 조회합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = NotificationListResponse.class,
			description = "알림 목록 조회 성공"
		)
	)
	ResponseEntity<ApiResponse<NotificationListResponse>> getNotifications(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
		@RequestParam(defaultValue = "0") final int page,
		@Parameter(description = "페이지 크기", example = "20")
		@RequestParam(defaultValue = "20") final int size
	);

	@Operation(
		summary = "알림 읽음 표시",
		description = "특정 알림을 읽음 상태로 표시합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "알림 읽음 표시 성공"
		)
	)
	ResponseEntity<ApiResponse<?>> markNotificationAsRead(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@PathVariable final Long notificationId
	);

	@Operation(
		summary = "모든 알림 읽음 표시",
		description = "사용자의 모든 알림을 읽음 상태로 표시합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "모든 알림 읽음 표시 성공"
		)
	)
	ResponseEntity<ApiResponse<?>> markAllNotificationsAsRead(
		@AuthenticationPrincipal final CustomUserDetails userDetails
	);
}
