package im.toduck.domain.events.social.presentation.api;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import im.toduck.domain.events.social.presentation.dto.request.EventsSocialRequest;
import im.toduck.domain.events.social.presentation.dto.response.EventsSocialCheckResponse;
import im.toduck.domain.events.social.presentation.dto.response.EventsSocialListResponse;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.swagger.ApiSuccessResponseExplanation;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "EventsSocial")
public interface EventsSocialApi {
	@Operation(
		summary = "참여 확인",
		description = """
			<p>사용자가 해당 날짜에 참여했는지 확인합니다.</p>
			<p>예시 : /v1/events-social/check?date=2025-09-21</p>
			<p>응답 - <br/>
			participated : true -> 이미 참여함 <br/>
			participated : false -> 참여하지 않음</p>
			"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = EventsSocialCheckResponse.class,
			description = "조회 완료"
		)
	)
	ResponseEntity<ApiResponse<EventsSocialCheckResponse>> checkUserParticipation(
		@RequestParam @Valid final LocalDate date,
		@AuthenticationPrincipal final CustomUserDetails userDetails
	);

	@Operation(
		summary = "참여 정보 저장",
		description = "사용자의 참여 정보를 저장합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "저장 완료. 빈 content 객체를 반환합니다."
		)
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> saveEventsSocial(
		@RequestBody @Valid final EventsSocialRequest request,
		@AuthenticationPrincipal final CustomUserDetails userDetails
	);

	@Operation(
		summary = "소셜 이벤트 참여 목록 반환",
		description = "소셜 이벤트 참여 목록을 반환합니다. 관리자(ADMIN)만 조회할 수 있습니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = EventsSocialListResponse.class,
			description = "소셜 이벤트 참여 목록 반환 완료. 목록을 반환합니다."
		)
	)
	ResponseEntity<ApiResponse<EventsSocialListResponse>> getEventsSocial(
		@AuthenticationPrincipal final CustomUserDetails userDetails
	);

	@Operation(
		summary = "소셜 이벤트 정보 삭제",
		description = "소셜 이벤트 정보를 삭제합니다. 관리자(ADMIN)만 삭제할 수 있습니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "소셜 이벤트 정보 삭제 완료. 빈 content를 반환합니다."
		)
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> deleteEventsSocial(
		@PathVariable final Long eventsSocialId,
		@AuthenticationPrincipal final CustomUserDetails userDetails
	);
}
