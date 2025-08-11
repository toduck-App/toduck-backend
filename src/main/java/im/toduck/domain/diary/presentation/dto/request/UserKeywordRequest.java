package im.toduck.domain.diary.presentation.dto.request;

import im.toduck.domain.diary.persistence.entity.KeywordCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "유저 키워드 생성 요청 DTO")
public record UserKeywordRequest(
	@NotNull(message = "카테고리는 비어있을 수 없습니다.")
	@Schema(description = "카테고리", example = "PLACE")
	KeywordCategory keywordCategory,

	@NotBlank(message = "키워드는 비어있을 수 없습니다.")
	@Size(max = 255, message = "키워드는 255자를 초과할 수 없습니다.")
	@Schema(description = "키워드", example = "회사")
	String keyword
) {

}
