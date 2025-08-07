package im.toduck.domain.diary.presentation.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "일기에 선택된 키워드 생성 요청 DTO")
public record DiaryKeywordCreateRequest(
	@NotNull(message = "일기 ID는 비어있을 수 없습니다.")
	@Schema(description = "일기 ID", example = "1")
	Long diaryId,

	@NotNull(message = "키워드 ID는 비어있을 수 없습니다.")
	@Schema(description = "키워드 ID", example = "[1, 5]")
	List<Long> keywordIds
) {
}
