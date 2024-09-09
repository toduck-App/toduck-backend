package im.toduck.domain.auth.presentation.controller;

import java.time.Duration;
import java.util.Map;

import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.auth.domain.usecase.OAuth2UseCase;
import im.toduck.domain.auth.presentation.dto.JwtPair;
import im.toduck.domain.auth.presentation.dto.request.LoginRequest;
import im.toduck.domain.auth.presentation.dto.request.SignUpRequest;
import im.toduck.domain.auth.presentation.dto.response.LoginResponse;
import im.toduck.global.oauth.OidcProvider;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.util.CookieUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/oauth")
@Slf4j
public class OAuthController {
	private final OAuth2UseCase oauth2UseCase;
	private final CookieUtil cookieUtil;
	private static final int REFRESH_TOKEN_EXPIRES_IN_DAYS = 7;

	@PostMapping("/login")
	@PreAuthorize("isAnonymous()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> oauthSignIn(@RequestBody @Valid LoginRequest request) {
		return ResponseEntity.ok(ApiResponse.createSuccessWithNoContent());
	}

	@PostMapping("/register")
	@PreAuthorize("isAnonymous()")
	public ResponseEntity<ApiResponse<LoginResponse>> oauthRegister(
		@RequestParam OidcProvider provider,
		@RequestBody @Valid SignUpRequest.Oidc request) {
		return createAuthResponse(oauth2UseCase.signUp(provider, request));
	}

	private ResponseEntity<ApiResponse<LoginResponse>> createAuthResponse(Pair<Long, JwtPair> userInfo) {
		ResponseCookie cookie = cookieUtil.createCookie("refreshToken", userInfo.getSecond().refreshToken(),
			Duration.ofDays(REFRESH_TOKEN_EXPIRES_IN_DAYS).toSeconds());

		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, cookie.toString())
			.body(ApiResponse.createSuccess(LoginResponse.of(userInfo.getSecond().accessToken(), userInfo.getFirst())));
	}
}
