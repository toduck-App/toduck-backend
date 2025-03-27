package im.toduck.domain.user.presentation.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.user.domain.usecase.UserFollowUseCase;
import im.toduck.domain.user.presentation.api.UserFollowApi;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserFollowController implements UserFollowApi {

	private final UserFollowUseCase userFollowUseCase;

	@Override
	@PostMapping("/{followedUserId}/follow")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> followUser(
		@PathVariable Long followedUserId,
		@AuthenticationPrincipal CustomUserDetails user
	) {
		userFollowUseCase.followUser(user.getUserId(), followedUserId);
		return ResponseEntity.ok(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@DeleteMapping("/{followedUserId}/follow")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> unfollowUser(
		@PathVariable Long followedUserId,
		@AuthenticationPrincipal CustomUserDetails user
	) {
		userFollowUseCase.unfollowUser(user.getUserId(), followedUserId);
		return ResponseEntity.ok(ApiResponse.createSuccessWithNoContent());
	}
}
