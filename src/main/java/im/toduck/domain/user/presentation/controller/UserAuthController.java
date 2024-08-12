package im.toduck.domain.user.presentation.controller;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.user.domain.usecase.UserAuthUseCase;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import im.toduck.global.util.CookieUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserAuthController {
	private final UserAuthUseCase userAuthUseCase;
	private final CookieUtil cookieUtil;

	@GetMapping("/logout")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> signOut(
		@RequestHeader("Authorization") String authHeader,
		@CookieValue(value = "refreshToken", required = false) String refreshToken,
		@AuthenticationPrincipal CustomUserDetails user
	) {
		String accessToken = authHeader.split(" ")[1];
		userAuthUseCase.signOut(user.getUserId(), accessToken, refreshToken);
		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, cookieUtil.deleteCookie("refreshToken").toString())
			.body(ApiResponse.createSuccessWithNoContent());
	}
}
