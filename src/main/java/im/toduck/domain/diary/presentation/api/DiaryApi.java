package im.toduck.domain.diary.presentation.api;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import im.toduck.domain.diary.presentation.dto.request.DiaryCreateRequest;
import im.toduck.domain.diary.presentation.dto.request.DiaryUpdateRequest;
import im.toduck.domain.diary.presentation.dto.response.DiaryCreateResponse;
import im.toduck.domain.diary.presentation.dto.response.DiaryResponse;
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

	@Operation(
		summary = "일기 수정",
		description =
			"""
				<b>일기 수정 API는 수정이 필요한 필드(날짜 제외)만 포함하여 요청할 수 있습니다.</b><br/><br/>
				<p>예시 요청:</p><br/>
				{<br/>
				"isChangeEmotion": true,<br/>
				"emotion": "HAPPY",<br/>
				"title": "수정된 제목입니다.",<br/>
				"memo": null,<br/>
				"diaryImageUrls": ["https://cdn.toduck.app/new-image.jpg"]<br/>
				}<br/><br/>
				<p>위 예시는 감정, 제목, 이미지를 수정하며, 메모는 변경하지 않는 경우입니다.</p><br/>
				<b>필드별 동작 방식:</b><br/>
				<p>- <b>isChangeEmotion</b>: emotion 값이 변경 됐는지 boolean 값으로 확인합니다. null 값이 들어갈 수 없습니다.</p>
				<p>- <b>emotion</b>: isChangeEmotion이 true인 경우에만 내용을 수정합니다. null 값이 들어갈 수 없습니다.</p>
				<p>- <b>title</b>: null이 아닌 경우에만 제목을 수정합니다. null인 경우 기존 제목이 유지됩니다.</p>
				<p>- <b>memo</b>: null이 아닌 경우에만 메모(일기)를 수정합니다. null인 경우 기존 메모(일기)가 유지됩니다.</p>
				<p>- <b>diaryImageUrls</b>: null인 경우 이미지를 수정하지 않습니다. 빈 배열([])을 전달하면 이미지를 모두 제거합니다.</p><br/>
				<b>감정 관련 시나리오 (isChangeEmotion, emotion은 필수값):</b><br/>
				<p>1. <b>감정 유지</b>: isChangeEmotion = false, emotion = 기존 감정</p>
				<p>2. <b>감정 변경</b>: isChangeEmotion = true, emotion = 변경할 감정(null인 경우 예외발생)</p><br/>
				<b>제목 관련 시나리오:</b><br/>
				<p>1. <b>제목 유지</b>: title = null</p>
				<p>2. <b>제목 변경</b>: title = 변경할 제목(지우고 싶은 경우 빈 문자열)</p>
				<b>메모(일기) 관련 시나리오:</b><br/>
				<p>1. <b>메모 유지</b>: memo = null</p>
				<p>2. <b>메모 수정</b>: memo = 변경할 내용(지우고 싶은 경우 빈 문자열)</p>
				<b>이미지 관련 시나리오:</b><br/>
				<p>1. <b>이미지 유지</b>: diaryImageUrls = null (이미지를 수정하지 않음)</p>
				<p>2. <b>이미지 추가/수정</b>: diaryImageUrls = [새로운 이미지 URL 리스트]</p>
				<p>3. <b>이미지 모두 제거</b>: diaryImageUrls = [] (기존 이미지를 모두 제거)</p>
				<p>4. <b>최대 이미지 초과</b>: diaryImageUrls = [이미지 URL 3개 이상] (예외 발생)</p><br/>
				"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			description = "일기 수정 성공, 빈 content 객체를 반환합니다."
		),
		errors = {
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.EMPTY_DIARY_EMOTION),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.NOT_FOUND_DIARY),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.UNAUTHORIZED_ACCESS_DIARY),
			@ApiErrorResponseExplanation(exceptionCode = ExceptionCode.INVALID_DIARY_EMOTION)
		}
	)
	ResponseEntity<ApiResponse<Map<String, Object>>> updateDiary(
		@PathVariable Long diaryId,
		@AuthenticationPrincipal DiaryUpdateRequest request,
		@AuthenticationPrincipal CustomUserDetails user
	);

	@Operation(
		summary = "특정 연월에 작성된 일기 검색",
		description =
			"""
				<b>특정 연월에 작성된 일기들을 조회합니다.</b><br/><br/>
				<p><b>연월 필터를 적용하는 방법:</b></p>
				<p>예시: /v1/diary?year=2025&month=3</p><br/>
				<p>- <b>year:</b> 조회 할 연도</p>
				<p>- <b>month:</b> 조회 할 달</p>
				<p>검색 결과가 존재하지 않는 경우 빈 배열이 반환됩니다.</p>
				"""
	)
	@ApiResponseExplanations(
		success = @ApiSuccessResponseExplanation(
			responseClass = DiaryResponse.class,
			description = "일기 조회 성공, 해당 연월에 작성된 일기들을 반환합니다."
		)
	)
	ResponseEntity<ApiResponse<List<DiaryResponse>>> getDiariesByMonth(
		@RequestParam("year") int year,
		@RequestParam("month") int month,
		@AuthenticationPrincipal CustomUserDetails user
	);
}
