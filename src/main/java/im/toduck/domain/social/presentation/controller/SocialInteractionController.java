package im.toduck.domain.social.presentation.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.social.domain.usecase.SocialInteractionUseCase;
import im.toduck.domain.social.presentation.api.SocialInteractionApi;
import im.toduck.domain.social.presentation.dto.request.CommentCreateRequest;
import im.toduck.domain.social.presentation.dto.request.ReportCreateRequest;
import im.toduck.domain.social.presentation.dto.response.CommentCreateResponse;
import im.toduck.domain.social.presentation.dto.response.CommentLikeCreateResponse;
import im.toduck.domain.social.presentation.dto.response.SocialLikeCreateResponse;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/socials")
public class SocialInteractionController implements SocialInteractionApi {
	private final SocialInteractionUseCase socialInteractionUseCase;

	@Override
	@PostMapping("/{socialId}/comments")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<CommentCreateResponse>> createComment(
		@PathVariable Long socialId,
		@RequestBody @Valid CommentCreateRequest request,
		CustomUserDetails user
	) {

		return ResponseEntity.ok()
			.body(
				ApiResponse.createSuccess(socialInteractionUseCase.createComment(user.getUserId(), socialId, request)));
	}

	@Override
	@DeleteMapping("/{socialId}/comments/{commentId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> deleteComment(
		@PathVariable Long socialId,
		@PathVariable Long commentId,
		CustomUserDetails user
	) {
		socialInteractionUseCase.deleteComment(user.getUserId(), socialId, commentId);

		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@PostMapping("/{socialId}/likes")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<SocialLikeCreateResponse>> createSocialLike(
		@PathVariable Long socialId,
		CustomUserDetails user
	) {

		return ResponseEntity.ok()
			.body(ApiResponse.createSuccess(socialInteractionUseCase.createSocialLike(user.getUserId(), socialId)));
	}

	@Override
	@DeleteMapping("/{socialId}/likes")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> deleteSocialLike(
		@PathVariable Long socialId,
		CustomUserDetails user
	) {
		socialInteractionUseCase.deleteSocialLike(user.getUserId(), socialId);

		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@PostMapping("/{socialId}/report")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> reportSocialBoard(
		@RequestBody @Valid ReportCreateRequest request,
		@PathVariable Long socialId,
		@AuthenticationPrincipal CustomUserDetails user
	) {
		socialInteractionUseCase.reportSocial(user.getUserId(), socialId, request);

		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@PostMapping("/{socialId}/comments/{commentId}/report")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> reportComment(
		@RequestBody @Valid ReportCreateRequest request,
		@PathVariable Long socialId,
		@PathVariable Long commentId,
		@AuthenticationPrincipal CustomUserDetails user
	) {
		socialInteractionUseCase.reportComment(user.getUserId(), commentId, request);

		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@PostMapping("/{socialId}/comments/{commentId}/likes")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<CommentLikeCreateResponse>> createCommentLike(
		@PathVariable Long socialId,
		@PathVariable Long commentId,
		@AuthenticationPrincipal CustomUserDetails user
	) {
		return ResponseEntity.ok()
			.body(ApiResponse.createSuccess(socialInteractionUseCase.createCommentLike(user.getUserId(), commentId)));
	}

	@Override
	@DeleteMapping("/{socialId}/comments/{commentId}/likes")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> deleteCommentLike(
		@PathVariable Long socialId,
		@PathVariable Long commentId,
		@AuthenticationPrincipal CustomUserDetails user
	) {
		socialInteractionUseCase.deleteCommentLike(user.getUserId(), commentId);
		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}
}
