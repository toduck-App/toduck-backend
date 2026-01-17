package im.toduck.domain.badge.presentation.api;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import im.toduck.domain.badge.presentation.dto.request.RepresentativeBadgeRequest;
import im.toduck.domain.badge.presentation.dto.response.BadgeListResponse;
import im.toduck.domain.badge.presentation.dto.response.BadgeResponse;
import im.toduck.global.annotation.swagger.ApiErrorResponseExplanation;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.swagger.ApiSuccessResponseExplanation;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Badges", description = "뱃지 관련 API")
public interface BadgeApi {

	@Operation(summary = "새로 획득한 뱃지 조회", description = "아직 확인하지 않은 획득 뱃지 목록을 조회하고, 읽음 처리합니다.")
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "미확인 뱃지 조회 성공. 확인하지 않은 뱃지 리스트를 반환합니다."
		)
	)
	ResponseEntity<ApiResponse<List<BadgeResponse>>> getNewBadges(
		@AuthenticationPrincipal CustomUserDetails userDetails
	);

	@Operation(summary = "내 뱃지 목록 조회", description = "보유한 뱃지 목록과 대표 뱃지 설정 정보를 조회합니다.")
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "내 뱃지 목록 조회 성공"
		)
	)
	@GetMapping
	ResponseEntity<ApiResponse<BadgeListResponse>> getMyBadgeList(
		@AuthenticationPrincipal CustomUserDetails userDetails
	);

	@Operation(summary = "대표 뱃지 설정", description = "사용자의 대표 뱃지를 설정하거나 변경합니다.")
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "대표 뱃지 설정 성공"
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_BADGE),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_OWNED_BADGE)
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> updateRepresentativeBadge(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@Valid @RequestBody RepresentativeBadgeRequest request
	);
}
