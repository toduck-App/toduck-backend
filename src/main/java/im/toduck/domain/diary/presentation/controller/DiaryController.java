package im.toduck.domain.diary.presentation.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.diary.domain.usecase.DiaryUseCase;
import im.toduck.domain.diary.presentation.api.DiaryApi;
import im.toduck.domain.diary.presentation.dto.request.DiaryCreateRequest;
import im.toduck.domain.diary.presentation.dto.request.DiaryUpdateRequest;
import im.toduck.domain.diary.presentation.dto.response.DiaryCreateResponse;
import im.toduck.domain.diary.presentation.dto.response.DiaryResponse;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/diary")
public class DiaryController implements DiaryApi {

	private final DiaryUseCase diaryUseCase;

	@Override
	@PostMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<DiaryCreateResponse>> createDiary(
		@RequestBody @Valid final DiaryCreateRequest request,
		@AuthenticationPrincipal final CustomUserDetails userDetails
	) {
		return ResponseEntity.ok()
			.body(ApiResponse.createSuccess(diaryUseCase.createDiary(userDetails.getUserId(), request))
			);
	}

	@Override
	@DeleteMapping("/{diaryId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> deleteDiary(
		@PathVariable Long diaryId,
		@AuthenticationPrincipal CustomUserDetails user
	) {
		diaryUseCase.deleteDiary(user.getUserId(), diaryId);

		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@PatchMapping("/{diaryId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> updateDiary(
		@PathVariable Long diaryId,
		@RequestBody @Valid DiaryUpdateRequest request,
		@AuthenticationPrincipal CustomUserDetails user
	) {
		diaryUseCase.updateDiary(user.getUserId(), diaryId, request);

		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<List<DiaryResponse>>> getDiariesByMonth(
		@RequestParam("year") int year,
		@RequestParam("month") int month,
		@AuthenticationPrincipal CustomUserDetails user
	) {
		List<DiaryResponse> diaries = diaryUseCase.getDiariesByMonth(user.getUserId(), year, month);
		return ResponseEntity.ok(ApiResponse.createSuccess(diaries));
	}

	@GetMapping("/count")
	@PreAuthorize("isAuthenticated()")
	@Override
	public ResponseEntity<ApiResponse<Integer>> getDiaryCountByMonth(
		@RequestParam("year") int year,
		@RequestParam("month") int month,
		@AuthenticationPrincipal CustomUserDetails user
	) {
		int diaryCount = diaryUseCase.getDiaryCountByMonth(user.getUserId(), year, month);
		return ResponseEntity.ok(ApiResponse.createSuccess(diaryCount));
	}
}
