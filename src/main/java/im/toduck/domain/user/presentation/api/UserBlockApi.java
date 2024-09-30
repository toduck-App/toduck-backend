package im.toduck.domain.user.presentation.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

import im.toduck.global.annotation.swagger.ApiErrorResponseExplanation;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.swagger.ApiSuccessResponseExplanation;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "UserBlock")
public interface UserBlockApi {

	@Operation(
		summary = "유저 차단",
		description = "지정된 유저를 차단합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "유저 차단 성공, 빈 content 객체를 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.CANNOT_BLOCK_SELF),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.ALREADY_BLOCKED),
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> blockUser(
		@PathVariable("blockedUserId") Long blockedUserId,
		@AuthenticationPrincipal CustomUserDetails user
	);

	@Operation(
		summary = "유저 차단 취소",
		description = "지정된 유저의 차단을 취소합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "유저 차단 취소 성공, 빈 content 객체를 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_BLOCK),
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> unblockUser(
		@PathVariable("blockedUserId") Long blockedUserId,
		@AuthenticationPrincipal CustomUserDetails user
	);
}
