package im.toduck.domain.badge.presentation.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.badge.common.dto.response.BadgeResponse;
import im.toduck.domain.badge.domain.usecase.BadgeUseCase;
import im.toduck.domain.badge.presentation.api.BadgeApi;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/badges")
@RequiredArgsConstructor
public class BadgeController implements BadgeApi {

	private final BadgeUseCase badgeUseCase;

	@Override
	@GetMapping("/new")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<List<BadgeResponse>>> getNewBadges(
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		List<BadgeResponse> responses = badgeUseCase.getNewBadges(userDetails.getUserId());
		return ResponseEntity.ok(ApiResponse.createSuccess(responses));
	}
}
