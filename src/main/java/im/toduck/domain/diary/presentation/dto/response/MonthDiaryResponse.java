package im.toduck.domain.diary.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record MonthDiaryResponse(
	@Schema(description = "특정 연월의 일기 개수 증감 (전월 대비)", example = "1")
	Integer count
) {
}
