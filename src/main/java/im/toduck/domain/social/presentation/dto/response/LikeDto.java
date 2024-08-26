package im.toduck.domain.social.presentation.dto.response;

import lombok.Builder;

@Builder
public record LikeDto(
	boolean isLiked,
	int likeCount
) {
}
