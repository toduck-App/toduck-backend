package im.toduck.domain.social.presentation.dto.request;

import java.util.List;

public record CreateSocialRequest(
	String content,
	Boolean isAnonymous,
	List<Long> socialCategoryIds,
	List<String> socialImageUrls
) {
}
