package im.toduck.domain.auth.presentation.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;

import im.toduck.domain.auth.presentation.dto.request.LoginRequest;
import im.toduck.domain.auth.presentation.dto.response.LoginResponse;
import im.toduck.global.annotation.swagger.ApiErrorResponseExplanation;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.swagger.ApiSuccessResponseExplanation;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.presentation.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Auth")
public interface AuthControllerApi {
	@Operation(
		summary = "사용자(일반) 로그인",
		description = "일반 사용자(서비스 회원가입 유저) 로그인을 수행합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(responseClass = LoginResponse.class, description = "AccessToken은 응답"
			+ "으로 제공되며, RefreshToken은 Cookie로 제공됩니다.\n"),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.INVALID_PHONE_NUMBER_OR_PASSWORD)
		}
	)
	ResponseEntity<ApiResponse<LoginResponse>> signIn(LoginRequest request);

	@Operation(
		summary = "AccessToken 재발급",
		description = "AccessToken 만료시 재발급을 수행합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(responseClass = LoginResponse.class, description = "AccessToken은 응답"
			+ "으로 제공되며, RefreshToken은 Cookie로 제공됩니다.\n RTR 방식이므로, AccessToken과 RefreshToken 모두를"
			+ "재 발급합니다. 로그인과 같은 방식으로 처리하면 됩니다."),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.EXPIRED_REFRESH_TOKEN),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.TAKEN_AWAY_TOKEN)
		}
	)
	ResponseEntity<?> refresh(@CookieValue("refreshToken") @Valid String refreshToken);
}
