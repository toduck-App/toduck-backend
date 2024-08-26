package im.toduck.domain.social.presentation.dto.response;

import lombok.Builder;

@Builder
public record SocialImageDto(
	Long id,
	String url
) {
}
