package im.toduck.domain.diary.presentation.dto.response;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record DiaryStreakResponse(
	@Schema(description = "연속 작성 일수", example = "3")
	Integer consecutiveDays,

	@Schema(description = "마지막 작성 날짜", example = "2025-08-12")
	LocalDate lastDiaryDate
) {
	public static DiaryStreakResponse toDiaryStreak(Integer consecutiveDays, LocalDate lastDiaryDate) {
		return DiaryStreakResponse.builder()
			.consecutiveDays(consecutiveDays)
			.lastDiaryDate(lastDiaryDate)
			.build();
	}
}
