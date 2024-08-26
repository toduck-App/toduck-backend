package im.toduck.domain.social.presentation.dto.response;

import lombok.Builder;

@Builder
public record CommentCreateResponse(
	Long socialCommentId
) {
	public static CommentCreateResponse from(Long socialCommentId) {
		return CommentCreateResponse.builder()
			.socialCommentId(socialCommentId)
			.build();
	}
}
