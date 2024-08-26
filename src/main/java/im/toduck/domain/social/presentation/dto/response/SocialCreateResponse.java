package im.toduck.domain.social.presentation.dto.response;

import lombok.Builder;

@Builder
public record SocialCreateResponse(
	Long socialId
) {
	public static SocialCreateResponse from(Long socialId) {
		return SocialCreateResponse.builder()
			.socialId(socialId)
			.build();
	}
}
