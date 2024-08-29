package im.toduck.domain.routine.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.routine.domain.usecase.RoutineUseCase;
import im.toduck.domain.routine.presentation.api.RoutineApi;
import im.toduck.domain.routine.presentation.dto.request.RoutineCreateRequest;
import im.toduck.domain.routine.presentation.dto.response.RoutineCreateResponse;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/routines")
public class RoutineController implements RoutineApi {

	private final RoutineUseCase routineUseCase;

	@PostMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<RoutineCreateResponse>> createRoutine(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@RequestBody @Valid final RoutineCreateRequest request
	) {
		return ResponseEntity.ok(
			ApiResponse.createSuccess(routineUseCase.createRoutine(userDetails.getUserId(), request))
		);
	}
}
