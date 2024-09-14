package im.toduck.domain.social.mapper;

import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.entity.SocialImageFile;
import im.toduck.domain.social.presentation.dto.response.SocialImageDto;

public class SocialImageFileMapper {
	public static SocialImageFile toSocialImageFile(Social socialBoard, String url) {
		return SocialImageFile.builder()
			.social(socialBoard)
			.url(url)
			.build();
	}

	public static SocialImageDto toSocialImageDto(SocialImageFile socialImageFile) {
		return SocialImageDto.builder()
			.id(socialImageFile.getId())
			.url(socialImageFile.getUrl())
			.build();
	}
}
