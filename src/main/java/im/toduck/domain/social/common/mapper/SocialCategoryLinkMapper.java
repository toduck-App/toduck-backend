package im.toduck.domain.social.common.mapper;

import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.entity.SocialCategory;
import im.toduck.domain.social.persistence.entity.SocialCategoryLink;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SocialCategoryLinkMapper {
	public static SocialCategoryLink toSocialCategoryLink(Social socialBoard, SocialCategory socialCategory) {
		return SocialCategoryLink.builder()
			.social(socialBoard)
			.socialCategory(socialCategory)
			.build();
	}
}
