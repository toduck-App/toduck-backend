package im.toduck.domain.diary.presentation.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

import im.toduck.domain.diary.presentation.dto.request.UserKeywordRequest;
import im.toduck.domain.diary.presentation.dto.response.UserKeywordListResponse;
import im.toduck.global.annotation.swagger.ApiErrorResponseExplanation;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.swagger.ApiSuccessResponseExplanation;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "UserKeyword")
public interface UserKeywordApi {
	@Operation(
		summary = "사용자 키워드 초기 설정",
		description = """
				1. 현재 로그인한 사용자의 키워드가 비어 있는 경우에만 실행됩니다.
				2. 마스터 키워드를 기반으로 user_keywords 테이블에 데이터를 복사합니다.
				3. 성공 시 content는 비어 있는 객체를 반환합니다.
				4. 이미 초기 설정이 돼있으면 ALREADY_SETUP_KEYWORD 예외를 반환합니다.
			"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "키워드 생성 성공, 빈 content 객체를 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.ALREADY_SETUP_KEYWORD)
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> setupUserKeyword(
		@AuthenticationPrincipal final CustomUserDetails userDetails
	);

	@Operation(
		summary = "사용자 키워드 생성",
		description = """
				1. 해당 사용자에게 키워드를 생성합니다.
				2. 동일한 키워드가 이미 존재하면 ALREADY_EXISTS_KEYWORD 예외를 반환합니다.
				3. 동일한 키워드가 이미 삭제된 경우 입력된 카테고리로 다시 생성됩니다.
			"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "키워드 생성 성공, 빈 content 객체를 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.ALREADY_EXISTS_KEYWORD)
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> createKeyword(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@RequestBody @Valid final UserKeywordRequest request
	);

	@Operation(
		summary = "사용자 키워드 삭제",
		description = """
				1. 해당 사용자의 키워드를 삭제합니다.
				2. 삭제하려는 키워드가 존재하지 않으면 NOT_FOUND_KEYWORD를 반환합니다.
			"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "키워드 생성 성공, 빈 content 객체를 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_KEYWORD)
		}
	)
	public ResponseEntity<ApiResponse<Map<String, Object>>> deleteKeyword(
		@RequestBody @Valid final UserKeywordRequest request,
		@AuthenticationPrincipal final CustomUserDetails userDetails
	);

	@Operation(
		summary = "사용자 키워드 검색",
		description = """
				1. 해당 사용자의 키워드 목록을 가져옵니다.
			"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "키워드 생성 성공, 해당 사용자의 키워드 목록을 반환합니다."
		),
		errors = {
		}
	)
	public ResponseEntity<ApiResponse<UserKeywordListResponse>> getKeyword(
		@AuthenticationPrincipal final CustomUserDetails userDetails
	);
}
