package im.toduck.domain.diary.presentation.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.diary.domain.usecase.DiaryUseCase;
import im.toduck.domain.diary.presentation.api.DiaryApi;
import im.toduck.domain.diary.presentation.dto.request.DiaryCreateRequest;
import im.toduck.domain.diary.presentation.dto.response.DiaryCreateResponse;
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
		return ResponseEntity.ok(
			ApiResponse.createSuccess(diaryUseCase.createDiary(userDetails.getUserId(), request))
		);
	}

	@Override
	@DeleteMapping("/{diaryId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> deleteDiary(
		@PathVariable Long diaryId,
		@AuthenticationPrincipal CustomUserDetails user
	) {
		diaryUseCase.deleteDiaryBoard(user.getUserId(), diaryId);

		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}
}
