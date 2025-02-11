package im.toduck.domain.schedule.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.schedule.domain.usecase.ScheduleUseCase;
import im.toduck.domain.schedule.presentation.api.ScheduleApi;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleCreateRequest;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleCreateResponse;
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
}
