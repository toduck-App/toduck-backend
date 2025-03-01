package im.toduck.domain.schedule.presentation.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.schedule.domain.usecase.ScheduleUseCase;
import im.toduck.domain.schedule.presentation.api.ScheduleApi;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleCompleteRequest;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleCreateRequest;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleDeleteRequest;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleCreateResponse;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleHeadResponse;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/schedules")
public class ScheduleController implements ScheduleApi {
	private final ScheduleUseCase scheduleUseCase;

	@PostMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<ScheduleCreateResponse>> createSchedule(
		@RequestBody @Valid ScheduleCreateRequest request,
		@AuthenticationPrincipal CustomUserDetails user
	) {
		return ResponseEntity.ok()
			.body(ApiResponse.createSuccess(scheduleUseCase.createSchedule(user.getUserId(), request)));
	}

	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<ScheduleHeadResponse>> getRangeSchedule(
		@AuthenticationPrincipal CustomUserDetails user,
		@RequestParam LocalDate startDate,
		@RequestParam LocalDate endDate
	) {
		return ResponseEntity.ok()
			.body(ApiResponse.createSuccess(scheduleUseCase.getRangeSchedule(user.getUserId(), startDate, endDate)));
	}

	@GetMapping("/{scheduleRecordId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<?>> getSchedule(
		@AuthenticationPrincipal CustomUserDetails user,
		@RequestParam Long scheduleRecordId
	) {
		return ResponseEntity.ok()
			.body(ApiResponse.createSuccess(scheduleUseCase.getSchedule(user.getUserId(), scheduleRecordId)));
	}

	@PostMapping("/is-complete")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> completeSchedule(
		@AuthenticationPrincipal CustomUserDetails user,
		@RequestBody ScheduleCompleteRequest scheduleCompleteRequest
	) {
		scheduleUseCase.completeSchedule(user.getUserId(), scheduleCompleteRequest);
		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}

	@DeleteMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> deleteSchedule(
		@AuthenticationPrincipal CustomUserDetails user,
		@RequestBody @Valid ScheduleDeleteRequest scheduleDeleteRequest
	) {
		scheduleUseCase.deleteSchedule(user.getUserId(), scheduleDeleteRequest);
		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}
}
