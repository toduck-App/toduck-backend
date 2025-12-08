package im.toduck.domain.diary.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.diary.domain.usecase.DiaryStreakUseCase;
import im.toduck.domain.diary.presentation.api.DiaryStreakApi;
import im.toduck.domain.diary.presentation.dto.response.DiaryStreakResponse;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/diary/streak")
public class DiaryStreakController implements DiaryStreakApi {
	private final DiaryStreakUseCase diaryStreakUseCase;

	@Override
	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<DiaryStreakResponse>> getDiaryStreak(
		@AuthenticationPrincipal CustomUserDetails user
	) {
		DiaryStreakResponse response = diaryStreakUseCase.getDiaryStreak(user.getUserId());
		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}

	@Override
	@GetMapping("/cached")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<DiaryStreakResponse>> getCachedDiaryStreak(
		@AuthenticationPrincipal CustomUserDetails user
	) {
		DiaryStreakResponse response = diaryStreakUseCase.getCachedDiaryStreak(user.getUserId());
		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}
}
