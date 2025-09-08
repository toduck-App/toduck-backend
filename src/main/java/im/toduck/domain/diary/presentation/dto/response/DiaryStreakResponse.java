package im.toduck.domain.diary.presentation.dto.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record DiaryStreakResponse(
	@Schema(description = "연속 작성 일수", example = "3")
	Long streak,

	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonFormat(pattern = "yyyy-MM-dd")
	@Schema(description = "마지막 작성 날짜", example = "2025-08-12")
	LocalDate lastDiaryDate
) {
}
