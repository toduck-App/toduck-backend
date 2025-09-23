package im.toduck.domain.events.detail.presentation.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.events.detail.domain.usecase.EventsDetailUseCase;
import im.toduck.domain.events.detail.presentation.api.EventsDetailApi;
import im.toduck.domain.events.detail.presentation.dto.request.EventsDetailCreateRequest;
import im.toduck.domain.events.detail.presentation.dto.request.EventsDetailUpdateRequest;
import im.toduck.domain.events.detail.presentation.dto.response.EventsDetailListResponse;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/events/detail")
public class EventsDetailController implements EventsDetailApi {

	private final EventsDetailUseCase eventsDetailUseCase;

	@Override
	@GetMapping("/get")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<EventsDetailListResponse>> getEventsDetails(
		@AuthenticationPrincipal final CustomUserDetails userDetails
	) {
		EventsDetailListResponse response = eventsDetailUseCase.getEventsDetails(userDetails.getUserId());

		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}

	@Override
	@PostMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> createEventsDetail(
		@RequestBody @Valid final EventsDetailCreateRequest request,
		@AuthenticationPrincipal final CustomUserDetails userDetails
	) {
		eventsDetailUseCase.createEventsDetail(request, userDetails.getUserId());

		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@PatchMapping("/{eventsDetailId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> updateEventsDetail(
		@PathVariable final Long eventsDetailId,
		@RequestBody @Valid final EventsDetailUpdateRequest request,
		@AuthenticationPrincipal final CustomUserDetails userDetails
	) {
		eventsDetailUseCase.updateEventsDetail(eventsDetailId, request, userDetails.getUserId());

		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@DeleteMapping("/{eventsDetailId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> deleteEventsDetail(
		@PathVariable final Long eventsDetailId,
		@AuthenticationPrincipal final CustomUserDetails userDetails
	) {
		eventsDetailUseCase.deleteEventsDetail(eventsDetailId, userDetails.getUserId());

		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}
}
