package im.toduck.domain.user.presentation.controller;

import static im.toduck.global.regex.UserRegex.*;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.user.domain.usecase.UserAuthUseCase;
import im.toduck.domain.user.presentation.api.UserAuthApi;
import im.toduck.domain.user.presentation.dto.request.ChangePasswordRequest;
import im.toduck.domain.user.presentation.dto.request.VerifyLoginIdPhoneNumberRequest;
import im.toduck.domain.user.presentation.dto.response.LoginIdResponse;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import im.toduck.global.util.CookieUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserAuthController implements UserAuthApi {
	private final UserAuthUseCase userAuthUseCase;
	private final CookieUtil cookieUtil;

	@Override
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

	@Override
	@GetMapping("/find/verified-code")
	@PreAuthorize("isAnonymous()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> sendVerifiedCodeForFind(
		@RequestParam("phoneNumber") @Pattern(regexp = PHONE_NUMBER_REGEXP) String phoneNumber) {
		userAuthUseCase.sendVerifiedCodeToPhoneNumberForFind(phoneNumber);
		return ResponseEntity.ok(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@GetMapping("/find/login-id")
	@PreAuthorize("isAnonymous()")
	public ResponseEntity<ApiResponse<LoginIdResponse>> findLoginId(
		@RequestParam("phoneNumber") @Pattern(regexp = PHONE_NUMBER_REGEXP) String phoneNumber
	) {
		return ResponseEntity.ok(
			ApiResponse.createSuccess(userAuthUseCase.findLoginId(phoneNumber)));
	}

	@Override
	@PatchMapping("/find/verify-login-id-phonenumber")
	@PreAuthorize("isAnonymous()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> verifyLoginIdPhoneNumber(
		@RequestBody @Valid VerifyLoginIdPhoneNumberRequest request
	) {
		userAuthUseCase.verifyLoginIdPhoneNumber(request);
		return ResponseEntity.ok(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@PutMapping("/find/change-password")
	@PreAuthorize("isAnonymous()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> changePassword(
		@RequestBody @Valid ChangePasswordRequest request
	) {
		userAuthUseCase.changePassword(request);
		return ResponseEntity.ok(ApiResponse.createSuccessWithNoContent());
	}

}
