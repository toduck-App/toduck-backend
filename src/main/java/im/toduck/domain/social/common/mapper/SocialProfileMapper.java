package im.toduck.domain.social.common.mapper;

import im.toduck.domain.social.presentation.dto.response.SocialProfileResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SocialProfileMapper {
	public static SocialProfileResponse toSocialProfileResponse(
		final String nickname,
		final int followingCount,
		final int followerCount,
		final int postCount,
		final boolean isMe,
		final boolean isFollowing

	) {
		return SocialProfileResponse.builder()
			.nickname(nickname)
			.followingCount(followingCount)
			.followerCount(followerCount)
			.postCount(postCount)
			.isMe(isMe)
			.isFollowing(isFollowing)
			.build();
	}
}
