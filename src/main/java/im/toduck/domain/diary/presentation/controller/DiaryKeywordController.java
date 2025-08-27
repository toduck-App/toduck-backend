package im.toduck.domain.diary.presentation.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.diary.domain.usecase.DiaryKeywordUseCase;
import im.toduck.domain.diary.presentation.api.DiaryKeywordApi;
import im.toduck.domain.diary.presentation.dto.request.DiaryKeywordCreateRequest;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/diaryKeyword")
public class DiaryKeywordController implements DiaryKeywordApi {

	private final DiaryKeywordUseCase diaryKeywordUseCase;

	@Override
	@PostMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> createDiaryKeyword(
		@RequestBody @Valid final DiaryKeywordCreateRequest request,
		@AuthenticationPrincipal final CustomUserDetails userDetails
	) {
		diaryKeywordUseCase.createDiaryKeyword(userDetails.getUserId(), request);
		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}
}
