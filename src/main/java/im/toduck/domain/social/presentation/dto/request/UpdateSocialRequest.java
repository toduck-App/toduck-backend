package im.toduck.domain.social.presentation.dto.request;

import java.util.List;

import im.toduck.global.annotation.valid.ValidCategoryIds;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;

public record UpdateSocialRequest(
	@Nullable
	@Size(min = 1, max = 255, message = "내용은 공백일 수 없으며 255자 이하여야 합니다.")
	String content,

	@Nullable
	Boolean isAnonymous,

	@ValidCategoryIds
	List<Long> socialCategoryIds,

	@Nullable
	@Size(max = 5, message = "이미지는 최대 5개까지만 등록할 수 있습니다.")
	List<String> socialImageUrls
) {
}
