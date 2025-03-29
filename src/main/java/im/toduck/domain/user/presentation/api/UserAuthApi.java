package im.toduck.domain.user.presentation.api;

import static im.toduck.global.regex.UserRegex.*;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import im.toduck.domain.user.presentation.dto.request.ChangePasswordRequest;
import im.toduck.domain.user.presentation.dto.request.VerifyLoginIdPhoneNumberRequest;
import im.toduck.domain.user.presentation.dto.response.LoginIdResponse;
import im.toduck.global.annotation.swagger.ApiErrorResponseExplanation;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.swagger.ApiSuccessResponseExplanation;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

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

	@Operation(
		summary = "아이디 찾기 혹은 비밀번호 찾기 인증번호 발송",
		description = "전화번호로 인증번호를 발송합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(description = "인증번호 발송 성공"),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_EXIST_PHONE_NUMBER),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.OVER_MAX_MESSAGE_COUNT),
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> sendVerifiedCodeForFind(
		@RequestParam("phoneNumber") @Pattern(regexp = PHONE_NUMBER_REGEXP) String phoneNumber);

	@Operation(
		summary = "아이디 찾기",
		description = "인증된 전화번호로 아이디를 찾습니다."
			+ "인증 API는 /v1/auth/check-verfied-code api 를 사용해주세요"
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(responseClass = LoginIdResponse.class,
			description = "로그인 아이디를 응답으로 제공합니다."),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_EXIST_PHONE_NUMBER),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_SEND_PHONE_NUMBER),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_VERIFIED_PHONE_NUMBER)
		}
	)
	ResponseEntity<ApiResponse<LoginIdResponse>> findLoginId(
		@RequestParam("phoneNumber") @Pattern(regexp = PHONE_NUMBER_REGEXP) String phoneNumber
	);

	@Operation(
		summary = "비밀번호 찾기 전화번호 및 로그인 id 유효성 검사 API",
		description = "검증된 전화번호 및 로그인 ID 인지 확인합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(description = "전화번호 및 로그인 ID가 유효합니다."),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_EXIST_PHONE_NUMBER),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.INVALID_LOGIN_ID),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_SEND_PHONE_NUMBER),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_VERIFIED_PHONE_NUMBER)
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> verifyLoginIdPhoneNumber(
		@RequestBody @Valid VerifyLoginIdPhoneNumberRequest request
	);

	@Operation(
		summary = "비밀번호 변경",
		description = "비밀번호 변경을 수행합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(description = "비밀번호 변경 성공"),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_EXIST_PHONE_NUMBER),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.INVALID_LOGIN_ID),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_SEND_PHONE_NUMBER),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_VERIFIED_PHONE_NUMBER)
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> changePassword(
		@RequestBody @Valid ChangePasswordRequest request
	);

}
