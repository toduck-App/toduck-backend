package im.toduck.domain.social.presentation.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import im.toduck.domain.social.presentation.dto.request.CommentCreateRequest;
import im.toduck.domain.social.presentation.dto.response.CommentCreateResponse;
import im.toduck.domain.social.presentation.dto.response.LikeCreateResponse;
import im.toduck.global.annotation.swagger.ApiErrorResponseExplanation;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.swagger.ApiSuccessResponseExplanation;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Social Interaction")
public interface SocialInteractionApi {
	@Operation(
		summary = "게시글 댓글 생성",
		description = "게시글 댓글을 작성합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = CommentCreateResponse.class,
			description = "댓글 작성 성공, 생성된 댓글의 Id를 반환합니다."
		),
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
		success = @ApiSuccessResponseExplanation(
			description = "댓글 삭제 성공, 빈 content 객체를 반환합니다."
		),
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
		success = @ApiSuccessResponseExplanation(
			responseClass = LikeCreateResponse.class,
			description = "좋아요 성공, 생성된 좋아요의 Id를 반환합니다."
		),
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
		success = @ApiSuccessResponseExplanation(
			description = "좋아요 취소 성공, 빈 content 객체를 반환합니다."
		),
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
}
