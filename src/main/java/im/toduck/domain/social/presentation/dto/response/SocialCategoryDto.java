package im.toduck.domain.social.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record SocialCategoryDto(
	@Schema(description = "소셜 카테고리 ID", example = "1")
	Long socialCategoryId,

	@Schema(description = "소셜 카테고리 이름", example = "집중력")
	String name
) {
}
