package im.toduck.domain.backoffice.presentation.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import im.toduck.domain.backoffice.presentation.dto.request.BroadcastNotificationCreateRequest;
import im.toduck.domain.backoffice.presentation.dto.response.BroadcastNotificationListResponse;
import im.toduck.domain.backoffice.presentation.dto.response.BroadcastNotificationResponse;
import im.toduck.global.annotation.swagger.ApiErrorResponseExplanation;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.swagger.ApiSuccessResponseExplanation;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.presentation.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "BackOffice Broadcast Notification")
public interface BroadcastNotificationApi {
	@Operation(
		summary = "브로드캐스트 알림 생성",
		description = "전체 회원에게 알림을 즉시 발송하거나 예약 발송합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = BroadcastNotificationResponse.class,
			description = "브로드캐스트 알림 생성 성공"
		)
	)
	ResponseEntity<ApiResponse<BroadcastNotificationResponse>> createBroadcastNotification(
		@RequestBody @Valid final BroadcastNotificationCreateRequest request
	);

	@Operation(
		summary = "브로드캐스트 알림 목록 조회",
		description = "모든 브로드캐스트 알림 이력을 조회합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = BroadcastNotificationListResponse.class,
			description = "브로드캐스트 알림 목록 조회 성공"
		)
	)
	ResponseEntity<ApiResponse<BroadcastNotificationListResponse>> getBroadcastNotifications();

	@Operation(
		summary = "브로드캐스트 알림 예약 취소",
		description = "예약된 브로드캐스트 알림을 취소합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "브로드캐스트 알림 예약 취소 성공"
		),
		errors = @ApiErrorResponseExplanation(
			exceptionCode = ExceptionCode.CANNOT_CANCEL_NOTIFICATION
		)
	)
	ResponseEntity<ApiResponse<?>> cancelBroadcastNotification(
		@Parameter(description = "브로드캐스트 알림 ID", example = "1")
		@PathVariable final Long id
	);
}
