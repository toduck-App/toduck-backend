package im.toduck.domain.social.presentation.dto.response;

import lombok.Builder;

@Builder
public record SocialProfileResponse(
	String nickname,
	int followingCount,
	int followerCount,
	int postCount,
	boolean isMe
) {
}
