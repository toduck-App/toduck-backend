package im.toduck.domain.schedule.presentation.api;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import im.toduck.domain.schedule.presentation.dto.request.ScheduleCompleteRequest;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleCreateRequest;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleDeleteRequest;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleModifyRequest;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleHeadResponse;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleIdResponse;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleInfoResponse;
import im.toduck.global.annotation.swagger.ApiErrorResponseExplanation;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.swagger.ApiSuccessResponseExplanation;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "일정 API")
public interface ScheduleApi {

	@Operation(
		summary = "일정 생성",
		description = """
			일정을 생성합니다.
			- 일정 생성 성공 시 생성된 일정의 Id를 반환합니다.
			"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = ScheduleIdResponse.class,
			description = "일정 생성 성공, 생성된 일정의 Id를 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_USER),
		}
	)
	ResponseEntity<ApiResponse<ScheduleIdResponse>> createSchedule(
		@RequestBody @Valid ScheduleCreateRequest request,
		@AuthenticationPrincipal CustomUserDetails user
	);

	@Operation(
		summary = "일정 기간 조회",
		description = """
			일정 기간 조회합니다.
			- 조회된 일정 목록과 일정 기록을 반환합니다.
			- 조회를 원하는 기간을(startDate, endDate) 지정합니다.
			- 기간에 해당하는 일정 목록과 일정 기록을 반환합니다.
			"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = ScheduleHeadResponse.class,
			description = "일정 기간 조회 성공, 조회된 일정 목록과 일정 기록을 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_USER),
		}
	)
	ResponseEntity<ApiResponse<ScheduleHeadResponse>> getRangeSchedule(
		@AuthenticationPrincipal CustomUserDetails user,
		@RequestParam LocalDate startDate,
		@RequestParam LocalDate endDate
	);

	@Operation(
		summary = "일정 단일 상세 조회",
		description = """
			일정 단일 상세 조회합니다.
			- 조회를 원하는 일정 기록의 Id를 지정합니다.(일정 기록 ID임 일정 ID가 아님)
			- 조회된 일정 기록과 일정 세부 정보를 반환합니다.
			"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = ScheduleInfoResponse.class,
			description = "일정 단일 상세 조회 성공, 조회된 일정 기록과 일정 세부 정보를 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_USER),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_SCHEDULE_RECORD),
		}
	)
	ResponseEntity<ApiResponse<?>> getSchedule(
		@AuthenticationPrincipal CustomUserDetails user,
		@RequestParam Long scheduleRecordId
	);

	@Operation(
		summary = "일정 완료 API",
		description = """
			일정 완료 상태를 변경합니다.
			- 완료 처리를 원하는 일정의 Id와 날짜를 지정합니다.
			- 일정 기록이 없다면 일정 기록이 생성됩니다.
			"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "일정 완료 상태 변경 성공"
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_USER),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_SCHEDULE),
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> completeSchedule(
		@AuthenticationPrincipal CustomUserDetails user,
		@RequestBody ScheduleCompleteRequest scheduleCompleteRequest
	);

	@Operation(
		summary = "일정 삭제 API",
		description = """
			일정 및 일정 기록을 삭제합니다.
			- 삭제를 원하는 일정의 Id와 삭제 기간을 지정합니다.
			- 하루 일정을 원할 경우 isOneDayDeleted true 입니다.
			- 특정 날짜 이후 삭제를 원할 경우 isOneDayDeleted false입니다 .
			"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "일정 삭제 성공"
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_USER),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_SCHEDULE),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NON_REPESTITIVE_ONE_SCHEDULE_NOT_PERIOD_DELETE),
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> deleteSchedule(
		@AuthenticationPrincipal CustomUserDetails user,
		@RequestBody @Valid ScheduleDeleteRequest scheduleDeleteRequest
	);

	@Operation(
		summary = "일정 수정 API",
		description = """
			일정을 수정합니다.
			- 수정을 원하는 일정의 Id와 수정을 원하는 날짜, 하루or이후 일정 수정 여부 그리고 수정할 정보를 지정합니다.
			- 수정된 일정의 Id를 반환합니다.
			"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = ScheduleIdResponse.class,
			description = "일정 수정 성공, 수정된 일정의 Id를 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_USER),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_SCHEDULE),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NON_REPESTITIVE_ONE_SCHEDULE_NOT_PERIOD_DELETE),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.PERIOD_SCHEDULE_CANNOT_AFTER_DATE_UPDATE),
		}
	)
	ResponseEntity<ApiResponse<ScheduleIdResponse>> updateSchedule(
		@AuthenticationPrincipal CustomUserDetails user,
		@RequestBody @Valid ScheduleModifyRequest request
	);

}
