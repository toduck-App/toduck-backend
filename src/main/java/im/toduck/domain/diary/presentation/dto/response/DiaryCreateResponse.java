package im.toduck.domain.diary.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "일기 생성 응답 DTO")
@Builder
public record DiaryCreateResponse(
	@Schema(description = "생성된 일기 Id", example = "1")
	Long diaryId
) {

}
