package im.toduck.domain.diary.presentation.dto.response;

import im.toduck.domain.diary.persistence.entity.KeywordCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record UserKeywordResponse(
	@Schema(description = "키워드 ID", example = "1")
	Long id,

	@Schema(description = "카테고리", example = "PLACE")
	KeywordCategory category,

	@Schema(description = "키워드", example = "회사")
	String keyword,

	@Schema(description = "사용 횟수", example = "3")
	Long count
) {

}
