package im.toduck.domain.social.presentation.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import im.toduck.domain.social.presentation.dto.request.CreateCommentRequest;
import im.toduck.domain.social.presentation.dto.request.CreateSocialRequest;
import im.toduck.domain.social.presentation.dto.request.UpdateSocialRequest;
import im.toduck.domain.social.presentation.dto.response.CreateCommentResponse;
import im.toduck.domain.social.presentation.dto.response.CreateSocialResponse;
import im.toduck.global.annotation.swagger.ApiErrorResponseExplanation;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

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

	@Operation(
		summary = "소셜 게시글 수정",
		description = "소셜 게시글을 수정합니다."
	)
	@ApiResponseExplanations(
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_SOCIAL_BOARD),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.UNAUTHORIZED_ACCESS_SOCIAL_BOARD),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_SOCIAL_CATEGORY),
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> updateSocialBoard(
		@PathVariable Long socialId,
		@RequestBody @Valid UpdateSocialRequest request,
		@AuthenticationPrincipal CustomUserDetails user
	);

	@Operation(
		summary = "게시글 댓글 생성",
		description = "게시글 댓글을 작성합니다."
	)
	@ApiResponseExplanations(
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_SOCIAL_BOARD),
		}
	)
	ResponseEntity<ApiResponse<CreateCommentResponse>> createComment(
		@PathVariable Long socialId,
		CreateCommentRequest request,
		@AuthenticationPrincipal CustomUserDetails user
	);

	@Operation(
		summary = "게시글 댓글 삭제",
		description = "게시글 댓글을 삭제합니다."
	)
	@ApiResponseExplanations(
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_SOCIAL_BOARD),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_COMMENT),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.UNAUTHORIZED_ACCESS_COMMENT),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.INVALID_COMMENT_FOR_BOARD),
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> deleteComment(
		@PathVariable Long socialId,
		@PathVariable Long commentId,
		@AuthenticationPrincipal CustomUserDetails user
	);

}
