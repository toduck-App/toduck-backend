package im.toduck.domain.social.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record SocialProfileResponse(
	@Schema(description = "사용자 닉네임", example = "뽀덕이")
	String nickname,

	@Schema(description = "프로필 이미지 URL", example = "https://cdn.toduck.app/profile.jpg")
	String profileImageUrl,

	@Schema(description = "팔로잉 수", example = "12")
	int followingCount,

	@Schema(description = "팔로워 수", example = "261")
	int followerCount,

	@Schema(description = "게시물 수", example = "315")
	int postCount,

	@Schema(description = "현재 사용자의 프로필인지 여부", example = "false")
	boolean isMe
) {
}
