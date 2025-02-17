package im.toduck.domain.schedule.presentation.api;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import im.toduck.domain.schedule.presentation.dto.request.ScheduleCreateRequest;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleCreateResponse;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleHeadResponse;
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
			responseClass = ScheduleCreateResponse.class,
			description = "일정 생성 성공, 생성된 일정의 Id를 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_USER),
		}
	)
	ResponseEntity<ApiResponse<ScheduleCreateResponse>> createSchedule(
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
}
