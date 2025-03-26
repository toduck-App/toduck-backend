package im.toduck.domain.social.presentation.dto.response;

public record SocialProfileResponse(
	String nickname,
	Long followingCount,
	Long followerCount,
	Long postCount,
	boolean isMe
) {
}
