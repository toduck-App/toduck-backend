package im.toduck.domain.auth.presentation.controller;

import java.time.Duration;

import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.auth.domain.usecase.AuthUseCase;
import im.toduck.domain.auth.presentation.dto.JwtPair;
import im.toduck.domain.auth.presentation.dto.request.LoginRequest;
import im.toduck.domain.auth.presentation.dto.response.LoginResponse;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.util.CookieUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthController {
	private static final int REFRESH_TOKEN_EXPIRES_IN_DAYS = 7;

	private final AuthUseCase authUseCase;
	private final CookieUtil cookieUtil;

	@PostMapping("/login")
	@PreAuthorize("isAnonymous()")
	public ResponseEntity<ApiResponse<LoginResponse>> signIn(@RequestBody @Valid LoginRequest request) {
		return createAuthResponse(authUseCase.signIn(request));
	}

	@GetMapping("/refresh")
	@PreAuthorize("isAnonymous()")
	public ResponseEntity<?> refresh(@CookieValue("refreshToken") @Valid String refreshToken) {
		return createAuthResponse(authUseCase.refresh(refreshToken));
	}

	private ResponseEntity<ApiResponse<LoginResponse>> createAuthResponse(Pair<Long, JwtPair> userInfo) {
		ResponseCookie cookie = cookieUtil.createCookie("refreshToken", userInfo.getSecond().refreshToken(),
			Duration.ofDays(REFRESH_TOKEN_EXPIRES_IN_DAYS).toSeconds());

		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, cookie.toString())
			.body(ApiResponse.createSuccess(LoginResponse.of(userInfo.getSecond().accessToken(), userInfo.getFirst())));
	}
}
