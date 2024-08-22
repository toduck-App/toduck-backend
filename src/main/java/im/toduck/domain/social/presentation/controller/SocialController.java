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

import im.toduck.domain.social.domain.usecase.SocialUseCase;
import im.toduck.domain.social.presentation.api.SocialControllerApi;
import im.toduck.domain.social.presentation.dto.request.CreateSocialRequest;
import im.toduck.domain.social.presentation.dto.response.CreateSocialResponse;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
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
		@RequestBody CreateSocialRequest request,
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
}