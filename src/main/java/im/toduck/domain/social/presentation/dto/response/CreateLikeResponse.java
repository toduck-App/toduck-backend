package im.toduck.domain.social.presentation.dto.response;

import lombok.Builder;

@Builder
public record CreateLikeResponse(
	Long likeId
) {
	public static CreateLikeResponse from(Long likeId) {
		return CreateLikeResponse.builder()
			.likeId(likeId)
			.build();
	}
}
