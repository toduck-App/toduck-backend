package im.toduck.domain.events.events.presentation.controller;

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

import im.toduck.domain.events.events.domain.usecase.EventsUseCase;
import im.toduck.domain.events.events.presentation.api.EventsApi;
import im.toduck.domain.events.events.presentation.dto.request.EventsCreateRequest;
import im.toduck.domain.events.events.presentation.dto.request.EventsUpdateRequest;
import im.toduck.domain.events.events.presentation.dto.response.EventsListResponse;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/events")
public class EventsController implements EventsApi {

	private final EventsUseCase eventsUseCase;

	@Override
	@GetMapping("/get")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<EventsListResponse>> getEvents(
		@AuthenticationPrincipal final CustomUserDetails userDetails
	) {
		EventsListResponse response = eventsUseCase.getEvents(userDetails.getUserId());

		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}

	@Override
	@PostMapping("/create")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<Map<String, Object>>> createEvents(
		@RequestBody @Valid final EventsCreateRequest request,
		@AuthenticationPrincipal final CustomUserDetails userDetails
	) {
		eventsUseCase.createEvents(request, userDetails.getUserId());

		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@PatchMapping("/{eventsId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<Map<String, Object>>> updateEvents(
		@PathVariable final Long eventsId,
		@RequestBody @Valid final EventsUpdateRequest request,
		@AuthenticationPrincipal final CustomUserDetails userDetails
	) {
		eventsUseCase.updateEvents(eventsId, request, userDetails.getUserId());

		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@DeleteMapping("/{eventsId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<Map<String, Object>>> deleteEvents(
		@PathVariable final Long eventsId,
		@AuthenticationPrincipal final CustomUserDetails userDetails
	) {
		eventsUseCase.deleteEvents(eventsId, userDetails.getUserId());

		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}
}
