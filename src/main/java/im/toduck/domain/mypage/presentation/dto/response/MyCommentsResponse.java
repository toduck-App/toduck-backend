package im.toduck.domain.mypage.presentation.dto.response;

import im.toduck.domain.social.presentation.dto.response.CommentDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "내 댓글 조회 응답")
public record MyCommentsResponse(
	@Schema(description = "소셜 게시글 ID", example = "123")
	Long socialId,

	@Schema(description = "댓글 정보")
	CommentDto comment
) {
}
