package im.toduck.domain.diary.presentation.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import im.toduck.domain.diary.presentation.dto.request.DiaryCreateRequest;
import im.toduck.domain.diary.presentation.dto.response.DiaryCreateResponse;
import im.toduck.global.annotation.swagger.ApiErrorResponseExplanation;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.swagger.ApiSuccessResponseExplanation;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Diary")
public interface DiaryApi {
	@Operation(
		summary = "일기 생성",
		description = "일기를 작성합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = DiaryCreateResponse.class,
			description = "일기 생성 성공, 생성된 일기의 Id를 반환합니다."
		)
	)
	ResponseEntity<ApiResponse<DiaryCreateResponse>> createDiary(
		@RequestBody @Valid DiaryCreateRequest request,
		@AuthenticationPrincipal CustomUserDetails userDetails
	);

	@Operation(
		summary = "일기 삭제",
		description = "일기를 삭제합니다."
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "일기 삭제 성공, 빈 content 객체를 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_DIARY),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.UNAUTHORIZED_ACCESS_DIARY)
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> deleteDiary(
		@PathVariable Long diaryId,
		@AuthenticationPrincipal CustomUserDetails user
	);
}
