package im.toduck.domain.social.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record LikeDto(
	@Schema(description = "사용자가 이 게시글을 좋아요 했는지 여부", example = "true")
	boolean isLiked,
	@Schema(description = "이 게시글의 총 좋아요 수", example = "13")
	int likeCount
) {
}
