package im.toduck.domain.events.social.presentation.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.events.social.domain.usecase.EventsSocialUseCase;
import im.toduck.domain.events.social.presentation.api.EventsSocialApi;
import im.toduck.domain.events.social.presentation.dto.request.EventsSocialRequest;
import im.toduck.domain.events.social.presentation.dto.response.EventsSocialCheckResponse;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/events-social")
public class EventsSocialController implements EventsSocialApi {

	private final EventsSocialUseCase eventsSocialUseCase;

	@Override
	@GetMapping("/check")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<EventsSocialCheckResponse>> checkUserParticipation(
		@RequestParam @Valid final LocalDate date,
		@AuthenticationPrincipal final CustomUserDetails userDetails
	) {
		EventsSocialCheckResponse response =
			eventsSocialUseCase.checkEventsSocial(date, userDetails.getUserId());

		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}

	@Override
	@PostMapping("/save")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> saveEventsSocial(
		@RequestBody @Valid final EventsSocialRequest request,
		@AuthenticationPrincipal final CustomUserDetails userDetails
	) {
		eventsSocialUseCase.saveEventsSocial(request, userDetails.getUserId());

		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}

	// 삭제도 추가해야 할듯. 업데이트도 해야하나? 업데이트는 굳이..?
	// 지울땐 어떤 정보를 기준으로 지워야 할까
}
