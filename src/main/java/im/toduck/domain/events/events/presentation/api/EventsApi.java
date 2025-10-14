package im.toduck.domain.events.events.presentation.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import im.toduck.domain.events.events.presentation.dto.request.EventsCreateRequest;
import im.toduck.domain.events.events.presentation.dto.request.EventsUpdateRequest;
import im.toduck.domain.events.events.presentation.dto.response.EventsListResponse;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.swagger.ApiSuccessResponseExplanation;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Events")
public interface EventsApi {
	@Operation(
		summary = "이벤트 목록 조회",
		description = "이벤트 목록을 조회합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = EventsListResponse.class,
			description = "이벤트 목록 조회 성공. 이벤트 목록을 반환합니다."
		)
	)
	ResponseEntity<ApiResponse<EventsListResponse>> getEvents(
		@AuthenticationPrincipal final CustomUserDetails userDetails
	);

	@Operation(
		summary = "이벤트 등록",
		description = "이벤트를 등록합니다. 관리자(ADMIN)만 등록할 수 있습니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "이벤트 등록 성공. 빈 content 객체를 반환합니다."
		)
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> createEvents(
		@RequestBody @Valid final EventsCreateRequest request,
		@AuthenticationPrincipal final CustomUserDetails userDetails
	);

	@Operation(
		summary = "이벤트 수정",
		description = "이벤트를 수정합니다. 관리자(ADMIN)만 수정할 수 있습니다. <br/> 수정할 값이 없으면 해당 값을 null로 설정해주세요."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "이벤트 수정 성공. 빈 content 객체를 반환합니다."
		)
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> updateEvents(
		@PathVariable final Long eventsId,
		@RequestBody @Valid final EventsUpdateRequest request,
		@AuthenticationPrincipal final CustomUserDetails userDetails
	);

	@Operation(
		summary = "이벤트 삭제",
		description = "이벤트를 삭제합니다. 관리자(ADMIN)만 삭제할 수 있습니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "이벤트 삭제 성공. 빈 content 객체를 반환합니다."
		)
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> deleteEvents(
		@PathVariable final Long eventsId,
		@AuthenticationPrincipal final CustomUserDetails userDetails
	);
}
