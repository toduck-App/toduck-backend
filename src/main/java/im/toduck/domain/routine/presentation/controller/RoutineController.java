package im.toduck.domain.routine.presentation.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.routine.domain.usecase.RoutineUseCase;
import im.toduck.domain.routine.presentation.api.RoutineApi;
import im.toduck.domain.routine.presentation.dto.request.RoutineCreateRequest;
import im.toduck.domain.routine.presentation.dto.request.RoutinePutCompletionRequest;
import im.toduck.domain.routine.presentation.dto.request.RoutineUpdateRequest;
import im.toduck.domain.routine.presentation.dto.response.MyRoutineAvailableListResponse;
import im.toduck.domain.routine.presentation.dto.response.MyRoutineRecordReadListResponse;
import im.toduck.domain.routine.presentation.dto.response.RoutineCreateResponse;
import im.toduck.domain.routine.presentation.dto.response.RoutineDetailResponse;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/routines")
public class RoutineController implements RoutineApi {

	private final RoutineUseCase routineUseCase;

	@Override
	@PostMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<RoutineCreateResponse>> postRoutine(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@RequestBody @Valid final RoutineCreateRequest request
	) {
		return ResponseEntity.ok(
			ApiResponse.createSuccess(routineUseCase.createRoutine(userDetails.getUserId(), request))
		);
	}

	@Override
	@GetMapping("/me")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<MyRoutineRecordReadListResponse>> getMyRoutineList(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
	) {
		return ResponseEntity.ok(
			ApiResponse.createSuccess(routineUseCase.readMyRoutineRecordList(userDetails.getUserId(), date))
		);
	}

	@Override
	@PutMapping("/{routineId}/completion")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<?>> putRoutineCompletion(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@PathVariable final Long routineId,
		@RequestBody @Valid final RoutinePutCompletionRequest request
	) {
		routineUseCase.updateRoutineCompletion(userDetails.getUserId(), routineId, request);

		return ResponseEntity.ok(
			ApiResponse.createSuccessWithNoContent()
		);
	}

	@Override
	@GetMapping("/{routineId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<RoutineDetailResponse>> getMyRoutineDetail(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@PathVariable final Long routineId
	) {
		return ResponseEntity.ok(
			ApiResponse.createSuccess(routineUseCase.readDetail(userDetails.getUserId(), routineId))
		);
	}

	@Override
	@GetMapping("/me/available")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<MyRoutineAvailableListResponse>> getMyAvailableRoutineList(
		@AuthenticationPrincipal final CustomUserDetails userDetails
	) {
		return ResponseEntity.ok(
			ApiResponse.createSuccess(routineUseCase.readMyAvailableRoutineList(userDetails.getUserId()))
		);
	}

	@Override
	@PutMapping("/{routineId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<?>> putRoutine(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@PathVariable final Long routineId,
		@RequestBody @Valid final RoutineUpdateRequest request
	) {
		routineUseCase.updateRoutine(userDetails.getUserId(), routineId, request);

		return ResponseEntity.ok(
			ApiResponse.createSuccessWithNoContent()
		);
	}

	@Override
	@DeleteMapping("/{routineId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<?>> deleteRoutine(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@PathVariable final Long routineId,
		@RequestParam final Boolean keepRecords
	) {
		routineUseCase.deleteRoutine(userDetails.getUserId(), routineId, keepRecords);

		return ResponseEntity.ok(
			ApiResponse.createSuccessWithNoContent()
		);
	}
}
