package im.toduck.domain.social.presentation.dto.response;

import lombok.Builder;

@Builder
public record SocialCategoryDto(
	Long id,
	String name
) {
}
