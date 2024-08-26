package im.toduck.domain.social.mapper;

import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.entity.SocialImageFile;
import im.toduck.global.annotation.Mapper;

@Mapper
public class SocialImageFileMapper {
	public static SocialImageFile toSocialImageFile(Social socialBoard, String url) {
		return SocialImageFile.builder()
			.social(socialBoard)
			.url(url)
			.build();
	}
}
