package im.toduck.domain.social.presentation.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.social.domain.usecase.SocialUseCase;
import im.toduck.domain.social.presentation.api.SocialControllerApi;
import im.toduck.domain.social.presentation.dto.request.CreateCommentRequest;
import im.toduck.domain.social.presentation.dto.request.CreateSocialRequest;
import im.toduck.domain.social.presentation.dto.request.UpdateSocialRequest;
import im.toduck.domain.social.presentation.dto.response.CreateCommentResponse;
import im.toduck.domain.social.presentation.dto.response.CreateLikeResponse;
import im.toduck.domain.social.presentation.dto.response.CreateSocialResponse;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/socials")
public class SocialController implements SocialControllerApi {
	private final SocialUseCase socialUseCase;

	@Override
	@PostMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<CreateSocialResponse>> createSocialBoard(
		@RequestBody @Valid CreateSocialRequest request,
		@AuthenticationPrincipal CustomUserDetails user) {

		return ResponseEntity.ok()
			.body(ApiResponse.createSuccess(socialUseCase.createSocialBoard(user.getUserId(), request)));
	}

	@Override
	@DeleteMapping("/{socialId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> deleteSocialBoard(
		@PathVariable Long socialId,
		@AuthenticationPrincipal CustomUserDetails user) {
		socialUseCase.deleteSocialBoard(user.getUserId(), socialId);

		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@PatchMapping("/{socialId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> updateSocialBoard(
		@PathVariable Long socialId,
		@RequestBody @Valid UpdateSocialRequest request,
		@AuthenticationPrincipal CustomUserDetails user) {
		socialUseCase.updateSocialBoard(user.getUserId(), socialId, request);

		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@PostMapping("/{socialId}/comments")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<CreateCommentResponse>> createComment(
		@PathVariable Long socialId,
		@RequestBody @Valid CreateCommentRequest request,
		CustomUserDetails user) {

		return ResponseEntity.ok()
			.body(ApiResponse.createSuccess(socialUseCase.createComment(user.getUserId(), socialId, request)));
	}

	@Override
	@DeleteMapping("/{socialId}/comments/{commentId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> deleteComment(
		@PathVariable Long socialId,
		@PathVariable Long commentId,
		CustomUserDetails user) {
		socialUseCase.deleteComment(user.getUserId(), socialId, commentId);

		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@PostMapping("/{socialId}/likes")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<CreateLikeResponse>> createLike(
		@PathVariable Long socialId,
		CustomUserDetails user) {

		return ResponseEntity.ok()
			.body(ApiResponse.createSuccess(socialUseCase.createLike(user.getUserId(), socialId)));
	}

	@Override
	@DeleteMapping("/{socialId}/likes/{likeId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> deleteLike(
		@PathVariable Long socialId,
		@PathVariable Long likeId,
		CustomUserDetails user) {
		socialUseCase.deleteLike(user.getUserId(), socialId, likeId);

		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}
}
