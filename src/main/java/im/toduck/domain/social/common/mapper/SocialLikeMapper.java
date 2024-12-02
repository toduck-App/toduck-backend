package im.toduck.domain.social.common.mapper;

import im.toduck.domain.social.persistence.entity.Like;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.presentation.dto.response.SocialLikeCreateResponse;
import im.toduck.domain.social.presentation.dto.response.SocialLikeDto;
import im.toduck.domain.user.persistence.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SocialLikeMapper {
	public static Like toLike(User user, Social socialBoard) {
		return Like.builder()
			.user(user)
			.social(socialBoard)
			.build();
	}

	public static SocialLikeCreateResponse toSocialLikeCreateResponse(Like like) {
		return SocialLikeCreateResponse.builder()
			.socialLikeId(like.getId())
			.build();
	}

	public static SocialLikeDto toSocialLikeDto(Social socialBoard, boolean isLiked) {
		return SocialLikeDto.builder()
			.isLikedByMe(isLiked)
			.likeCount(socialBoard.getLikeCount())
			.build();
	}
}
