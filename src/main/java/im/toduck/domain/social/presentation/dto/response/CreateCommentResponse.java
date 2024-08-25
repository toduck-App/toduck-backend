package im.toduck.domain.social.presentation.dto.response;

import lombok.Builder;

@Builder
public record CreateCommentResponse(
	Long socialCommentId
) {
	public static CreateCommentResponse from(Long socialCommentId) {
		return CreateCommentResponse.builder()
			.socialCommentId(socialCommentId)
			.build();
	}
}
