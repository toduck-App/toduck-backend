package im.toduck.domain.social.common.mapper;

import im.toduck.domain.social.persistence.entity.Like;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.presentation.dto.response.LikeCreateResponse;
import im.toduck.domain.social.presentation.dto.response.LikeDto;
import im.toduck.domain.user.persistence.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LikeMapper {
	public static Like toLike(User user, Social socialBoard) {
		return Like.builder()
			.user(user)
			.social(socialBoard)
			.build();
	}

	public static LikeCreateResponse toLikeCreateResponse(Like like) {
		return LikeCreateResponse.builder()
			.likeId(like.getId())
			.build();
	}

	public static LikeDto toLikeDto(Social socialBoard, boolean isLiked) {
		return LikeDto.builder()
			.isLiked(isLiked)
			.likeCount(socialBoard.getLikeCount())
			.build();
	}
}
