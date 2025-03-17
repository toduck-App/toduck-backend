package im.toduck.domain.mypage.presentation.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

import im.toduck.domain.mypage.presentation.dto.request.NickNameUpdateRequest;
import im.toduck.global.annotation.swagger.ApiErrorResponseExplanation;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.swagger.ApiSuccessResponseExplanation;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "My Page")
public interface MyPageApi {
	@Operation(
		summary = "닉네임 변경",
		description = "사용자 닉네임을 변경합니다. 닉네임은 알파벳, 한글, 숫자로만 이루어진 2~8자의 문자열이어야 합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "닉네임 변경 성공, 빈 content 객체를 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.EXISTS_USER_NICKNAME)
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> updateNickname(
		@RequestBody @Valid NickNameUpdateRequest request,
		@AuthenticationPrincipal CustomUserDetails userDetails
	);

}
