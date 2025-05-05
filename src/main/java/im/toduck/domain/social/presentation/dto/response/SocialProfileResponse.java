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

	@Schema(description = "총 루틴 공유 수", example = "1304")
	int totalRoutineShareCount,

	@Schema(description = "작성한 댓글 수", example = "42")
	int commentCount,

	@Schema(description = "현재 사용자의 프로필인지 여부", example = "false")
	boolean isMe,

	@Schema(description = "팔로잉 여부", example = "true")
	boolean isFollowing
) {
}
