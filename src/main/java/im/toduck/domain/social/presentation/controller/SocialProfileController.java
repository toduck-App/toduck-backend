package im.toduck.domain.social.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import im.toduck.domain.routine.presentation.dto.request.RoutineCreateRequest;
import im.toduck.domain.routine.presentation.dto.response.RoutineCreateResponse;
import im.toduck.domain.social.domain.usecase.SocialProfileUseCase;
import im.toduck.domain.social.presentation.api.SocialProfileApi;
import im.toduck.domain.social.presentation.dto.response.SocialProfileResponse;
import im.toduck.domain.social.presentation.dto.response.SocialResponse;
import im.toduck.domain.social.presentation.dto.response.UserProfileRoutineListResponse;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.presentation.dto.response.CursorPaginationResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/profiles")
public class SocialProfileController implements SocialProfileApi {
	private final SocialProfileUseCase socialProfileUseCase;

	@Override
	@GetMapping("/{userId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<SocialProfileResponse>> getUserProfile(
		@PathVariable Long userId,
		@AuthenticationPrincipal CustomUserDetails authUser
	) {
		SocialProfileResponse profileResponse = socialProfileUseCase.getUserProfile(userId, authUser.getUserId());
		return ResponseEntity.ok(ApiResponse.createSuccess(profileResponse));
	}

	@Override
	@GetMapping("/{userId}/socials")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<CursorPaginationResponse<SocialResponse>>> getUserSocials(
		@PathVariable Long userId,
		@AuthenticationPrincipal CustomUserDetails authUser,
		@RequestParam(required = false) Long cursor,
		@RequestParam(required = false) Integer limit
	) {
		CursorPaginationResponse<SocialResponse> userSocials = socialProfileUseCase.getUserSocials(
			userId,
			authUser.getUserId(),
			cursor,
			limit
		);
		return ResponseEntity.ok(ApiResponse.createSuccess(userSocials));
	}

	@Override
	@GetMapping("/{userId}/routines")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<UserProfileRoutineListResponse>> getUserProfileRoutines(
		@PathVariable Long userId,
		@AuthenticationPrincipal CustomUserDetails authUser
	) {
		UserProfileRoutineListResponse response = socialProfileUseCase.readUserAvailableRoutines(
			userId,
			authUser.getUserId()
		);
		return ResponseEntity.ok(ApiResponse.createSuccess(response));
	}

	@Override
	@PostMapping("/shared-routines/{routineId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<RoutineCreateResponse>> saveSharedRoutine(
		@PathVariable Long routineId,
		@AuthenticationPrincipal CustomUserDetails authUser,
		@RequestBody RoutineCreateRequest request
	) {
		return ResponseEntity.ok(
			ApiResponse.createSuccess(socialProfileUseCase.saveSharedRoutine(authUser.getUserId(), routineId, request))
		);
	}
}
