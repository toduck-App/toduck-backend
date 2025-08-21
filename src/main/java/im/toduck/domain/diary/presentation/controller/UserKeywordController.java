package im.toduck.domain.diary.presentation.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.diary.domain.usecase.UserKeywordUseCase;
import im.toduck.domain.diary.presentation.api.UserKeywordApi;
import im.toduck.domain.diary.presentation.dto.request.UserKeywordRequest;
import im.toduck.domain.diary.presentation.dto.response.UserKeywordListResponse;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user-keywords")
public class UserKeywordController implements UserKeywordApi {

	private final UserKeywordUseCase userKeywordUseCase;

	@Override
	@PostMapping("/create")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> createKeyword(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@RequestBody @Valid final UserKeywordRequest request
	) {
		userKeywordUseCase.createKeyword(userDetails.getUserId(), request);
		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@DeleteMapping("/delete")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> deleteKeyword(
		@RequestBody @Valid final UserKeywordRequest request,
		@AuthenticationPrincipal final CustomUserDetails userDetails
	) {
		userKeywordUseCase.deleteKeyword(userDetails.getUserId(), request);
		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<UserKeywordListResponse>> getKeyword(
		@AuthenticationPrincipal final CustomUserDetails userDetails
	) {
		UserKeywordListResponse response = userKeywordUseCase.getKeywords(userDetails.getUserId());
		return ResponseEntity.ok().body(ApiResponse.createSuccess(response));
	}
}
