package im.toduck.domain.mypage.presentation.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.mypage.domain.usecase.MyPageUseCase;
import im.toduck.domain.mypage.presentation.api.MyPageApi;
import im.toduck.domain.mypage.presentation.dto.request.NickNameUpdateRequest;
import im.toduck.domain.mypage.presentation.dto.response.NickNameResponse;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/my-page")
public class MyPageController implements MyPageApi {
	private final MyPageUseCase myPageUseCase;

	@Override
	@PatchMapping("/nickname")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<Map<String, Object>>> updateNickname(
		@RequestBody @Valid NickNameUpdateRequest request,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		myPageUseCase.updateNickname(userDetails.getUserId(), request);
		return ResponseEntity.ok(ApiResponse.createSuccessWithNoContent());
	}

	@Override
	@GetMapping("/nickname")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<NickNameResponse>> getMyNickname(
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		NickNameResponse response = myPageUseCase.getMyNickname(userDetails.getUserId());
		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}

}
