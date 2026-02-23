package im.toduck.domain.auth.presentation.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.auth.domain.usecase.WebLoginUseCase;
import im.toduck.domain.auth.presentation.api.WebLoginApi;
import im.toduck.domain.auth.presentation.dto.request.WebLoginAuthorizeRequest;
import im.toduck.domain.auth.presentation.dto.response.WebSessionCreateResponse;
import im.toduck.domain.auth.presentation.dto.response.WebSessionStatusResponse;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth/web")
public class WebLoginController implements WebLoginApi {
	private final WebLoginUseCase webLoginUseCase;

	@Override
	@PostMapping("/sessions")
	@PreAuthorize("isAnonymous()")
	public ResponseEntity<ApiResponse<WebSessionCreateResponse>> createWebSession() {
		WebSessionCreateResponse response = webLoginUseCase.createWebSession();
		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}

	@Override
	@PostMapping("/authorize")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<?>> authorizeWebSession(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@RequestBody @Valid final WebLoginAuthorizeRequest request
	) {
		String role = extractRoleFromAuthorities(userDetails);
		boolean authorized = webLoginUseCase.authorizeWebSession(
			userDetails.getUserId(),
			role,
			request
		);
		return ResponseEntity.ok(ApiResponse.createSuccess(Map.of("authorized", authorized)));
	}

	private String extractRoleFromAuthorities(final CustomUserDetails userDetails) {
		return userDetails.getAuthorities().stream()
			.findFirst()
			.map(authority -> authority.getAuthority().replace("ROLE_", ""))
			.orElse("USER");
	}

	@Override
	@GetMapping("/sessions/{sessionToken}")
	@PreAuthorize("isAnonymous()")
	public ResponseEntity<ApiResponse<WebSessionStatusResponse>> getWebSessionStatus(
		@PathVariable final String sessionToken
	) {
		WebSessionStatusResponse response = webLoginUseCase.getWebSessionStatus(sessionToken);
		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}
}
