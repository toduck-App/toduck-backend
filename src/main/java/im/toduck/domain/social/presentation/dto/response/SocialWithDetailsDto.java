package im.toduck.domain.social.presentation.dto.response;

import java.util.List;

import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.entity.SocialImageFile;
import im.toduck.domain.social.presentation.dto.response.SocialCategoryResponse.SocialCategoryDto;
import lombok.Builder;

@Builder
public record SocialWithDetailsDto(
	Social social,

	List<SocialImageFile> imageFiles,
	Integer commentCount,
	Boolean isLikedByCurrentUser,
	List<SocialCategoryDto> categories
) {
}
