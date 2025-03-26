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

@Tag(name = "UserFollow")
public interface UserFollowApi {

	@Operation(
		summary = "유저 팔로우",
		description = "지정된 유저를 팔로우합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "유저 팔로우 성공, 빈 content 객체를 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.CANNOT_FOLLOW_SELF),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.ALREADY_FOLLOWING),
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> followUser(
		@PathVariable("followedUserId") Long followedUserId,
		@AuthenticationPrincipal CustomUserDetails user
	);

	@Operation(
		summary = "유저 언팔로우",
		description = "지정된 유저의 팔로우를 취소합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "유저 언팔로우 성공, 빈 content 객체를 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_FOLLOW)
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> unfollowUser(
		@PathVariable("followedUserId") Long followedUserId,
		@AuthenticationPrincipal CustomUserDetails user
	);
}
