package im.toduck.domain.diary.presentation.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.diary.domain.usecase.UserKeywordUseCase;
import im.toduck.domain.diary.presentation.api.UserKeywordApi;
import im.toduck.domain.diary.presentation.dto.request.UserKeywordCreateRequest;
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
	@PostMapping("/setup")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> setupUserKeyword(
		@AuthenticationPrincipal final CustomUserDetails userDetails
	) {
		userKeywordUseCase.setupKeyword(userDetails.getUserId());
		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@PostMapping("/create")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> createKeyword(
		@AuthenticationPrincipal final CustomUserDetails userDetails,
		@RequestBody @Valid final UserKeywordCreateRequest request
	) {
		userKeywordUseCase.createKeyword(userDetails.getUserId(), request);
		return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	}

	// @Override
	// @DeleteMapping("/delete")
	// @PreAuthorize("isAuthenticated()")
	// public ResponseEntity<ApiResponse<Map<String, Object>>> deleteKeyword(
	// 	@RequestBody @Valid final KeywordDelete request,
	// 	@AuthenticationPrincipal final CustomUserDetails userDetails
	// ) {
	// 	keywordUseCase.deleteKeyword(userDetails.getUserId(), request);
	// 	return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	// }
	//
	// @Override
	// @GetMapping()
	// @PreAuthorize("isAuthenticated()")
	// public ResponseEntity<ApiResponse<Map<String, Object>>> getKeyword(
	// 	@AuthenticationPrincipal final CustomUserDetails userDetails
	// ) {
	// 	keywordUseCase.getKeyword(userDetails.getUserId());
	// 	return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoContent());
	// }

	// 이미 가입된 사람들 중에 키워드 없는 사람들한테도 키워드 넣어줘야함
}
