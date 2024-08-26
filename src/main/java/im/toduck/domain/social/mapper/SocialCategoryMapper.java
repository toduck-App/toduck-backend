package im.toduck.domain.social.mapper;

import im.toduck.domain.social.persistence.entity.SocialCategory;
import im.toduck.domain.social.presentation.dto.response.SocialCategoryDto;
import im.toduck.global.annotation.Mapper;

@Mapper
public class SocialCategoryMapper {
	public static SocialCategoryDto toSocialCategoryDto(SocialCategory socialCategory) {
		return SocialCategoryDto.builder()
			.id(socialCategory.getId())
			.name(socialCategory.getName())
			.build();
	}
}
