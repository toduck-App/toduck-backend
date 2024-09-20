package im.toduck.domain.auth.presentation.controller;

import static im.toduck.global.regex.UserRegex.*;

import java.time.Duration;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.auth.domain.usecase.AuthUseCase;
import im.toduck.domain.auth.domain.usecase.GeneralSignUpUseCase;
import im.toduck.domain.auth.domain.usecase.OAuth2UseCase;
import im.toduck.domain.auth.presentation.api.AuthControllerApi;
import im.toduck.domain.auth.presentation.dto.JwtPair;
import im.toduck.domain.auth.presentation.dto.request.LoginRequest;
import im.toduck.domain.auth.presentation.dto.request.SignUpRequest;
import im.toduck.domain.auth.presentation.dto.response.LoginResponse;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.util.CookieUtil;
import im.toduck.infra.oauth.OidcProvider;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthController implements AuthControllerApi {
	private static final int REFRESH_TOKEN_EXPIRES_IN_DAYS = 7;

	private final AuthUseCase authUseCase;
	private final GeneralSignUpUseCase generalSignUpUseCase;
	private final OAuth2UseCase oauth2UseCase;
	private final CookieUtil cookieUtil;

	@PostMapping("/login")
	@PreAuthorize("isAnonymous()")
	public ResponseEntity<ApiResponse<LoginResponse>> signIn(@RequestBody @Valid LoginRequest request) {
		return createAuthResponse(authUseCase.signIn(request));
	}

	@Override
	@GetMapping("/refresh")
	@PreAuthorize("isAnonymous()")
	public ResponseEntity<?> refresh(@CookieValue("refreshToken") @Valid String refreshToken) {
		return createAuthResponse(authUseCase.refresh(refreshToken));
	}

	@GetMapping("/verified-code")
	@PreAuthorize("isAnonymous()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> sendVerifiedCode(
		@RequestParam("phoneNumber") @Pattern(regexp = PHONE_NUMBER_REGEXP) String phoneNumber) {
		generalSignUpUseCase.sendVerifiedCodeToPhoneNumber(phoneNumber);
		return ResponseEntity.ok(ApiResponse.createSuccessWithNoContent());
	}

	@GetMapping("/check-verfied-code")
	@PreAuthorize("isAnonymous()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> checkVerifiedCode(
		@RequestParam("phoneNumber") @Pattern(regexp = PHONE_NUMBER_REGEXP) String phoneNumber,
		@RequestParam("verifiedCode") @Pattern(regexp = VERIFIED_CODE_REGEXP) String verifiedCode) {
		generalSignUpUseCase.checkVerifiedCode(phoneNumber, verifiedCode);
		return ResponseEntity.ok(ApiResponse.createSuccessWithNoContent());
	}

	@GetMapping("/check-user-id")
	@PreAuthorize("isAnonymous()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> checkLoginId(
		@RequestParam("loginId") @Pattern(regexp = LOGIN_ID_REGEXP) String loginId) {
		generalSignUpUseCase.checkLoginId(loginId);
		return ResponseEntity.ok(ApiResponse.createSuccessWithNoContent());
	}

	@PostMapping("/register")
	@PreAuthorize("isAnonymous()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> register(
		@RequestBody @Valid SignUpRequest.General request) {
		generalSignUpUseCase.signUp(request);
		return ResponseEntity.ok(ApiResponse.createSuccessWithNoContent());
	}

	@PostMapping("/oauth/register")
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
