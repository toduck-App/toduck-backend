package im.toduck.domain.auth.presentation.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.auth.presentation.dto.request.LoginRequest;
import im.toduck.domain.auth.presentation.dto.request.SignUpRequest;
import im.toduck.global.presentation.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/oauth")
@Slf4j
public class OAuthController {
	@PostMapping("/login")
	@PreAuthorize("isAnonymous()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> oauthSignIn(@RequestBody @Valid LoginRequest request) {
		return ResponseEntity.ok(ApiResponse.createSuccessWithNoContent());
	}

	@PostMapping("/register")
	@PreAuthorize("isAnonymous()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> oauthRegister(
		@RequestBody @Valid SignUpRequest.Oidc request) {
		return ResponseEntity.ok(ApiResponse.createSuccessWithNoContent());
	}
}
