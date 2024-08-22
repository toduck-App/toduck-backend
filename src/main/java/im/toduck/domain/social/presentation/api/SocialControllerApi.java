package im.toduck.domain.social.presentation.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

import im.toduck.domain.social.presentation.dto.request.CreateSocialRequest;
import im.toduck.domain.social.presentation.dto.response.CreateSocialResponse;
import im.toduck.global.annotation.swagger.ApiErrorResponseExplanation;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Social")
public interface SocialControllerApi {
	@Operation(
		summary = "소셜 게시글 생성",
		description = "소셜 게시글을 작성합니다."
	)
	@ApiResponseExplanations(
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_SOCIAL_CATEGORY),
		}
	)
	ResponseEntity<ApiResponse<CreateSocialResponse>> createSocialBoard(
		CreateSocialRequest request,
		@AuthenticationPrincipal CustomUserDetails user
	);

	@Operation(
		summary = "소셜 게시글 삭제",
		description = "소셜 게시글을 삭제합니다."
	)
	@ApiResponseExplanations(
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_SOCIAL_BOARD),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.UNAUTHORIZED_ACCESS_SOCIAL_BOARD)
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> deleteSocialBoard(
		@PathVariable Long socialId,
		@AuthenticationPrincipal CustomUserDetails user
	);
}
