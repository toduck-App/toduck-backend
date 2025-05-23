package im.toduck.domain.auth.presentation.api;

import static im.toduck.global.regex.UserRegex.*;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import im.toduck.domain.auth.presentation.dto.request.LoginRequest;
import im.toduck.domain.auth.presentation.dto.request.SignUpRequest;
import im.toduck.domain.auth.presentation.dto.response.LoginResponse;
import im.toduck.global.annotation.swagger.ApiErrorResponseExplanation;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.swagger.ApiSuccessResponseExplanation;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.infra.oauth.OidcProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

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
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.INVALID_LOGIN_ID_OR_PASSWORD)
		}
	)
	ResponseEntity<ApiResponse<LoginResponse>> signIn(@RequestBody @Valid LoginRequest request);

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

	@Operation(
		summary = "인증번호 발송",
		description = "전화번호로 인증번호를 발송합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(description = "인증번호 발송 성공\n"
			+ "인증번호는 5자리 숫자이며 메시지로 전송됩니다.\n"
			+ "현재 인증번호는 유효시간에만 인증할 수 있습니다."),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.EXISTS_PHONE_NUMBER),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.OVER_MAX_MESSAGE_COUNT),
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> sendVerifiedCode(
		@RequestParam("phoneNumber") @Pattern(regexp = PHONE_NUMBER_REGEXP) String phoneNumber);

	@Operation(
		summary = "인증번호 확인",
		description = "전화번호로 발송된 인증번호를 확인합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(description = "인증번호 확인 성공\n"
			+ "인증번호가 일치하면 인증 완료 처리됩니다."),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_SEND_PHONE_NUMBER),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.OVER_MAX_VERIFIED_COUNT),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.INVALID_VERIFIED_CODE),
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> checkVerifiedCode(
		@RequestParam("phoneNumber") @Pattern(regexp = PHONE_NUMBER_REGEXP) String phoneNumber,
		@RequestParam("verifiedCode") @Pattern(regexp = VERIFIED_CODE_REGEXP) String verifiedCode);

	@Operation(
		summary = "loginId 중복 확인",
		description = "loginId 중복 확인을 수행합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(description = "loginId 중복 확인 성공\n"
			+ "userId가 중복되지 않으면 성공합니다."),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.EXISTS_USER_ID),
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> checkLoginId(
		@RequestParam("loginId") @Pattern(regexp = LOGIN_ID_REGEXP) String loginId);

	@Operation(
		summary = "회원가입",
		description = "회원가입을 수행합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(description = "회원가입 성공\n"
			+ "회원가입이 완료되면 성공합니다."),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.EXISTS_USER_ID),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.EXISTS_PHONE_NUMBER),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_SEND_PHONE_NUMBER),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_VERIFIED_PHONE_NUMBER),
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> register(@RequestBody @Valid SignUpRequest.General request);

	@Operation(
		summary = "OAuth 회원가입",
		description = "OAuth OIDC 회원가입을 수행합니다. \n"
			+ "이미 회원가입이 완료된 User라면 로그인 완료 됩니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(responseClass = LoginResponse.class, description = "인증이 완료된다면"
			+ "AccessToken은 응답으로 제공되며, RefreshToken은 Cookie로 제공됩니다.\n"),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_MATCHED_PUBLIC_KEY),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.INVALID_ID_TOKEN),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.ABNORMAL_ID_TOKEN),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.INVALID_USER_FILED),
		}
	)
	public ResponseEntity<ApiResponse<LoginResponse>> oauthRegister(
		@RequestParam OidcProvider provider,
		@RequestBody @Valid SignUpRequest.Oidc request);
}
