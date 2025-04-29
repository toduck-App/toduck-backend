package im.toduck.domain.mypage.presentation.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "차단한 유저 목록 응답")
@Builder
public record BlockedUsersResponse(
	@Schema(description = "차단한 유저 목록")
	List<BlockedUser> blockedUsers
) {
	@Schema(description = "차단한 유저 정보")
	@Builder
	public record BlockedUser(
		@Schema(description = "차단한 유저 ID", example = "1")
		Long userId,

		@Schema(description = "차단한 유저 닉네임", example = "뽀덕잉")
		String nickname,

		@Schema(description = "차단한 유저 프로필 이미지 URL", example = "https://cdn.toduck.app/profile.jpg")
		String profileImageUrl
	) {
	}
}
