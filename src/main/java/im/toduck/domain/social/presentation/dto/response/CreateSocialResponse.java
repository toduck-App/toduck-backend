package im.toduck.domain.social.presentation.dto.response;

import lombok.Builder;

@Builder
public record CreateSocialResponse(
	Long socialId
) {
	public static CreateSocialResponse from(Long socialId) {
		return CreateSocialResponse.builder()
			.socialId(socialId)
			.build();
	}
}
