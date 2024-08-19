package im.toduck.domain.social.presentation.dto.request;

import java.util.List;

import im.toduck.domain.social.persistence.entity.SocialTag;

public record CreateSocialRequest(
	String content,
	SocialTag socialTag,
	Boolean isAnonymous,
	List<Long> socialCategoryIds,
	List<String> socialImageUrls
) {
}
