package im.toduck.domain.social.presentation.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.social.domain.usecase.SocialUseCase;
import im.toduck.domain.social.presentation.api.SocialApi;
import im.toduck.domain.social.presentation.dto.request.CommentCreateRequest;
import im.toduck.domain.social.presentation.dto.request.SocialCreateRequest;
import im.toduck.domain.social.presentation.dto.request.SocialUpdateRequest;
import im.toduck.domain.social.presentation.dto.response.CommentCreateResponse;
import im.toduck.domain.social.presentation.dto.response.LikeCreateResponse;
import im.toduck.domain.social.presentation.dto.response.SocialCreateResponse;
import im.toduck.domain.social.presentation.dto.response.SocialDetailResponse;
import im.toduck.domain.social.presentation.dto.response.SocialResponse;
import im.toduck.global.annotation.valid.PaginationLimit;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.presentation.dto.response.CursorPaginationResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/socials")
public class SocialController implements SocialApi {
	private final SocialUseCase socialUseCase;

	@Override
	@PostMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<SocialCreateResponse>> createSocialBoard(
		@RequestBody @Valid SocialCreateRequest request,
		@AuthenticationPrincipal CustomUserDetails user
	) {

		return ResponseEntity.ok()
			.body(ApiResponse.createSuccess(socialUseCase.createSocialBoard(user.getUserId(), request)));
	}

	@Override
	@DeleteMapping("/{socialId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> deleteSocialBoard(
		@PathVariable Long socialId,
		@AuthenticationPrincipal CustomUserDetails user
	) {
		socialUseCase.deleteSocialBoard(user.getUserId(), socialId);

		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@PatchMapping("/{socialId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> updateSocialBoard(
		@PathVariable Long socialId,
		@RequestBody @Valid SocialUpdateRequest request,
		@AuthenticationPrincipal CustomUserDetails user
	) {
		socialUseCase.updateSocialBoard(user.getUserId(), socialId, request);

		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@PostMapping("/{socialId}/comments")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<CommentCreateResponse>> createComment(
		@PathVariable Long socialId,
		@RequestBody @Valid CommentCreateRequest request,
		CustomUserDetails user
	) {

		return ResponseEntity.ok()
			.body(ApiResponse.createSuccess(socialUseCase.createComment(user.getUserId(), socialId, request)));
	}

	@Override
	@DeleteMapping("/{socialId}/comments/{commentId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> deleteComment(
		@PathVariable Long socialId,
		@PathVariable Long commentId,
		CustomUserDetails user
	) {
		socialUseCase.deleteComment(user.getUserId(), socialId, commentId);

		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@PostMapping("/{socialId}/likes")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<LikeCreateResponse>> createLike(
		@PathVariable Long socialId,
		CustomUserDetails user
	) {

		return ResponseEntity.ok()
			.body(ApiResponse.createSuccess(socialUseCase.createLike(user.getUserId(), socialId)));
	}

	@Override
	@DeleteMapping("/{socialId}/likes")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> deleteLike(
		@PathVariable Long socialId,
		CustomUserDetails user
	) {
		socialUseCase.deleteLike(user.getUserId(), socialId);

		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@GetMapping("/{socialId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<SocialDetailResponse>> getSocialDetail(
		@PathVariable Long socialId,
		CustomUserDetails user
	) {

		return ResponseEntity.ok()
			.body(ApiResponse.createSuccess(socialUseCase.getSocialDetail(user.getUserId(), socialId)));
	}

	@Override
	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<CursorPaginationResponse<SocialResponse>>> getSocials(
		CustomUserDetails user,
		Long cursor,
		@PaginationLimit Integer limit
	) {

		return ResponseEntity.ok()
			.body(ApiResponse.createSuccess(socialUseCase.getSocials(user.getUserId(), cursor, limit)));
	}
}
