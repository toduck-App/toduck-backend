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

import im.toduck.domain.user.domain.usecase.UserBlockUseCase;
import im.toduck.domain.user.presentation.api.UserBlockApi;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserBlockController implements UserBlockApi {
	private UserBlockUseCase userBlockUseCase;

	@Override
	@PostMapping("/{blockedUserId}/block")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> blockUser(
		@PathVariable Long blockedUserId,
		@AuthenticationPrincipal CustomUserDetails user
	) {
		userBlockUseCase.blockUser(user.getUserId(), blockedUserId);

		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@DeleteMapping("/{blockedUserId}/block")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> unblockUser(
		@PathVariable Long blockedUserId,
		@AuthenticationPrincipal CustomUserDetails user
	) {
		userBlockUseCase.unblockUser(user.getUserId(), blockedUserId);

		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}
}
