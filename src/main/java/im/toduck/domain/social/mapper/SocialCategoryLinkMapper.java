package im.toduck.domain.social.mapper;

import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.entity.SocialCategory;
import im.toduck.domain.social.persistence.entity.SocialCategoryLink;
import im.toduck.global.annotation.Mapper;

@Mapper
public class SocialCategoryLinkMapper {
	public static SocialCategoryLink toSocialCategoryLink(Social socialBoard, SocialCategory socialCategory) {
		return SocialCategoryLink.builder()
			.social(socialBoard)
			.socialCategory(socialCategory)
			.build();
	}
}
