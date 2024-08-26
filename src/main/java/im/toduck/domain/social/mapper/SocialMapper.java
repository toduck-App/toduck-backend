package im.toduck.domain.social.mapper;

import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.presentation.dto.response.SocialCreateResponse;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.annotation.Mapper;

@Mapper
public class SocialMapper {
	public static Social toSocial(User user, String content, Boolean isAnonymous) {
		return Social.builder()
			.user(user)
			.content(content)
			.isAnonymous(isAnonymous)
			.build();
	}

	public static SocialCreateResponse toSocialCreateResponse(Social socialBoard) {
		return SocialCreateResponse.builder()
			.socialId(socialBoard.getId())
			.build();
	}
}
