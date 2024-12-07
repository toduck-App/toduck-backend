package im.toduck.domain.social.presentation.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "카테고리 목록 응답")
@Builder
public record SocialCategoryResponse(
	@Schema(description = "카테고리 목록")
	List<SocialCategoryDto> categories
) {

	@Schema(description = "카테고리 응답 내부 DTO")
	@Builder
	public record SocialCategoryDto(
		@Schema(description = "카테고리 ID", example = "1")
		Long socialCategoryId,

		@Schema(description = "카테고리 이름", example = "집중력")
		String name
	) {
	}
}
