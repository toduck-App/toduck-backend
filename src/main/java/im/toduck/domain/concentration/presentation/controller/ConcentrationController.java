package im.toduck.domain.concentration.presentation.controller;

import java.time.YearMonth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.concentration.domain.usecase.ConcentrationUseCase;
import im.toduck.domain.concentration.presentation.api.ConcentrationApi;
import im.toduck.domain.concentration.presentation.dto.request.ConcentrationRequest;
import im.toduck.domain.concentration.presentation.dto.response.ConcentrationListResponse;
import im.toduck.domain.concentration.presentation.dto.response.ConcentrationPercentResponse;
import im.toduck.domain.concentration.presentation.dto.response.ConcentrationSaveResponse;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/concentration")
public class ConcentrationController implements ConcentrationApi {
	private final ConcentrationUseCase concentrationUseCase;

	@Override
	@PostMapping("/save")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<ConcentrationSaveResponse>> saveConcentration(
		@RequestBody @Valid ConcentrationRequest request,
		@AuthenticationPrincipal CustomUserDetails user
	) {
		return ResponseEntity.ok()
			.body(ApiResponse.createSuccess(concentrationUseCase.saveConcentration(user.getUserId(), request)));
	}

	@Override
	@GetMapping("/monthly")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<ConcentrationListResponse>> getMonthlyConcentration(
		@RequestParam YearMonth yearMonth,
		@AuthenticationPrincipal CustomUserDetails user
	) {
		ConcentrationListResponse response = concentrationUseCase.getMonthlyConcentration(user.getUserId(), yearMonth);
		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}

	@Override
	@GetMapping("/percent")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<ConcentrationPercentResponse>> getMonthConcentrationPercent(
		@RequestParam YearMonth yearMonth,
		@AuthenticationPrincipal CustomUserDetails user
	) {
		ConcentrationPercentResponse response = concentrationUseCase.getMonthConcentrationPercent(user.getUserId(),
			yearMonth);
		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}
}
