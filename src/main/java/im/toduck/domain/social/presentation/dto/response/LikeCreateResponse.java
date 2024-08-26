package im.toduck.domain.social.presentation.dto.response;

import lombok.Builder;

@Builder
public record LikeCreateResponse(
	Long likeId
) {
	public static LikeCreateResponse from(Long likeId) {
		return LikeCreateResponse.builder()
			.likeId(likeId)
			.build();
	}
}
