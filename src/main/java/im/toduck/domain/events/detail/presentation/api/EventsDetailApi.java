package im.toduck.domain.events.detail.presentation.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import im.toduck.domain.events.detail.presentation.dto.request.EventsDetailCreateRequest;
import im.toduck.domain.events.detail.presentation.dto.request.EventsDetailUpdateRequest;
import im.toduck.domain.events.detail.presentation.dto.response.EventsDetailListResponse;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.swagger.ApiSuccessResponseExplanation;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "EventsDetail")
public interface EventsDetailApi {
	@Operation(
		summary = "이벤트 디테일 목록 조회",
		description = "이벤트의 디테일 목록을 조회합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = EventsDetailListResponse.class,
			description = "이벤트 디테일 목록 조회 성공. 이벤트 디테일 목록을 반환합니다."
		)
	)
	ResponseEntity<ApiResponse<EventsDetailListResponse>> getEventsDetails(
		@AuthenticationPrincipal final CustomUserDetails userDetails
	);

	@Operation(
		summary = "이벤트 디테일 생성",
		description = "이벤트의 상세 내용을 생성합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "이벤트 디테일 생성 성공. 빈 content 객체를 반환합니다."
		)
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> createEventsDetail(
		@RequestBody @Valid final EventsDetailCreateRequest request,
		@AuthenticationPrincipal final CustomUserDetails userDetails
	);

	@Operation(
		summary = "이벤트 디테일 수정",
		description =
			"""
				이벤트 디테일의 내용을 수정합니다. <br/>
				"eventsId" : 이벤트의 일련번호 <br/>
				"routingUrl" : 연결 url <br/>
				"eventsDetailImgs" : 이벤트 상세 이미지 url <br/><br/>
				수정하지 않을 경우 null을 입력하면 됩니다. 이미지의 경우 빈 리스트를 넣으면 이미지가 전부 삭제됩니다.
				"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "이벤트 디테일 수정 성공. 빈 content 객체를 반환합니다."
		)
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> updateEventsDetail(
		@PathVariable final Long eventsDetailId,
		@RequestBody @Valid final EventsDetailUpdateRequest request,
		@AuthenticationPrincipal final CustomUserDetails userDetails
	);

	@Operation(
		summary = "이벤트 디테일 삭제",
		description = "이벤트 디테일을 삭제합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "이벤트 디테일 삭제 성공. 빈 content 객체를 반환합니다."
		)
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> deleteEventsDetail(
		@PathVariable final Long eventsDetailId,
		@AuthenticationPrincipal final CustomUserDetails userDetails
	);
}
