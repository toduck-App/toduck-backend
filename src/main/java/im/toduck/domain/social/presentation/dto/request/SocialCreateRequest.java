package im.toduck.domain.social.presentation.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SocialCreateRequest(
	@NotBlank(message = "내용은 공백일 수 없습니다.")
	@Size(max = 255, message = "내용은 255자 이하여야 합니다.")
	String content,

	@NotNull(message = "익명 여부는 필수 입력 항목입니다.")
	Boolean isAnonymous,

	@NotEmpty(message = "하나 이상의 카테고리는 필수로 입력해야 합니다.")
	List<Long> socialCategoryIds,

	@Size(max = 5, message = "이미지는 최대 5개까지만 등록할 수 있습니다.")
	List<String> socialImageUrls
) {
}
