package im.toduck.domain.social.common.mapper;

import im.toduck.domain.social.persistence.entity.SocialCategory;
import im.toduck.domain.social.presentation.dto.response.SocialCategoryDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SocialCategoryMapper {
	public static SocialCategoryDto toSocialCategoryDto(SocialCategory socialCategory) {
		return SocialCategoryDto.builder()
			.socialCategoryId(socialCategory.getId())
			.name(socialCategory.getName())
			.build();
	}
}
