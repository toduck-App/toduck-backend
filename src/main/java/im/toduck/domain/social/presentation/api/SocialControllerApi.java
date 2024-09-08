package im.toduck.domain.social.presentation.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import im.toduck.domain.social.presentation.dto.request.CommentCreateRequest;
import im.toduck.domain.social.presentation.dto.request.SocialCreateRequest;
import im.toduck.domain.social.presentation.dto.request.SocialUpdateRequest;
import im.toduck.domain.social.presentation.dto.response.CommentCreateResponse;
import im.toduck.domain.social.presentation.dto.response.LikeCreateResponse;
import im.toduck.domain.social.presentation.dto.response.SocialCreateResponse;
import im.toduck.domain.social.presentation.dto.response.SocialDetailResponse;
import im.toduck.domain.social.presentation.dto.response.SocialResponse;
import im.toduck.global.annotation.swagger.ApiErrorResponseExplanation;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.valid.PaginationLimit;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.presentation.dto.response.CursorPaginationResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
	ResponseEntity<ApiResponse<SocialCreateResponse>> createSocialBoard(
		@RequestBody @Valid SocialCreateRequest request,
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
		@RequestBody @Valid SocialUpdateRequest request,
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
	ResponseEntity<ApiResponse<CommentCreateResponse>> createComment(
		@PathVariable Long socialId,
		@RequestBody @Valid CommentCreateRequest request,
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

	@Operation(
		summary = "게시글 좋아요",
		description = "게시글을 좋아요 합니다."
	)
	@ApiResponseExplanations(
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.EXISTS_LIKE),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_SOCIAL_BOARD),
		}
	)
	ResponseEntity<ApiResponse<LikeCreateResponse>> createLike(
		@PathVariable Long socialId,
		@AuthenticationPrincipal CustomUserDetails user
	);

	@Operation(
		summary = "게시글 좋아요 취소",
		description = "게시글 좋아요를 취소합니다."
	)
	@ApiResponseExplanations(
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_SOCIAL_BOARD),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_LIKE),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.UNAUTHORIZED_ACCESS_LIKE),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.INVALID_LIKE_FOR_BOARD),

		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> deleteLike(
		@PathVariable Long socialId,
		@AuthenticationPrincipal CustomUserDetails user
	);

	@Operation(
		summary = "게시글 단건 조회",
		description = "게시글 단건 세부사항을 조회합니다."
	)
	@ApiResponseExplanations(
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_SOCIAL_BOARD)
		}
	)
	ResponseEntity<ApiResponse<SocialDetailResponse>> getSocialDetail(
		@PathVariable Long socialId,
		@AuthenticationPrincipal CustomUserDetails user
	);

	@Operation(
		summary = "게시글 목록 조회",
		description = "게시글을 커서 기반 페이지네이션으로 조회합니다."
	)
	@ApiResponseExplanations
	ResponseEntity<ApiResponse<CursorPaginationResponse<SocialResponse>>> getSocials(
		@AuthenticationPrincipal CustomUserDetails user,
		@Parameter(description = "조회를 시작할 커서 값") @RequestParam(required = false) Long after,
		@Parameter(description = "한 페이지에 표시할 항목 수") @PaginationLimit @RequestParam(required = false) Integer limit
	);

}
