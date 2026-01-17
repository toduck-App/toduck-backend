package im.toduck.domain.badge.presentation.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import im.toduck.domain.badge.common.dto.response.BadgeResponse;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.swagger.ApiSuccessResponseExplanation;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

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
}
