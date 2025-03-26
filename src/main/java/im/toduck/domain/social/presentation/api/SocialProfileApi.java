package im.toduck.domain.social.presentation.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

import im.toduck.domain.social.presentation.dto.response.SocialProfileResponse;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.security.authentication.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Social Profile")
public interface SocialProfileApi {

	@Operation(
		summary = "유저 프로필 조회",
		description = "userId를 이용하여 사용자의 프로필 정보를 조회합니다."
	)
	ResponseEntity<ApiResponse<SocialProfileResponse>> getUserProfile(
		@PathVariable Long userId,
		@AuthenticationPrincipal CustomUserDetails authUser
	);
}
