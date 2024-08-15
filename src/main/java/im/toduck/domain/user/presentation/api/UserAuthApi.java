package im.toduck.domain.user.presentation.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestHeader;

import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "UserAuth")
public interface UserAuthApi {
	@Operation(
		summary = "사용자 로그아웃",
		description = "로그아웃을 수행합니다."
	)
	@ApiResponseExplanations
	ResponseEntity<ApiResponse<Map<String, Object>>> signOut(
		@RequestHeader("Authorization") String authHeader,
		@CookieValue(value = "refreshToken", required = false) String refreshToken,
		@AuthenticationPrincipal CustomUserDetails user
	);
}
