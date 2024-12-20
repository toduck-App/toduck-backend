package im.toduck.domain.social.common.mapper;

import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.entity.SocialImageFile;
import im.toduck.domain.social.presentation.dto.response.SocialImageDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SocialImageFileMapper {
	public static SocialImageFile toSocialImageFile(Social socialBoard, String url) {
		return SocialImageFile.builder()
			.social(socialBoard)
			.url(url)
			.build();
	}

	public static SocialImageDto toSocialImageDto(SocialImageFile socialImageFile) {
		return SocialImageDto.builder()
			.socialImageId(socialImageFile.getId())
			.url(socialImageFile.getUrl())
			.build();
	}
}
