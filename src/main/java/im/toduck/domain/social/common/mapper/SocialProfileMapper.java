package im.toduck.domain.social.common.mapper;

import im.toduck.domain.social.presentation.dto.response.SocialProfileResponse;
import im.toduck.domain.user.persistence.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SocialProfileMapper {
	public static SocialProfileResponse toSocialProfileResponse(
		final User profileUser,
		final int followingCount,
		final int followerCount,
		final int postCount,
		final int totalRoutineShareCount,
		final int commentCount,
		final boolean isMe,
		final boolean isFollowing

	) {
		return SocialProfileResponse.builder()
			.nickname(profileUser.getNickname())
			.profileImageUrl(profileUser.getImageUrl())
			.followingCount(followingCount)
			.followerCount(followerCount)
			.postCount(postCount)
			.totalRoutineShareCount(totalRoutineShareCount)
			.commentCount(commentCount)
			.isMe(isMe)
			.isFollowing(isFollowing)
			.build();
	}
}
