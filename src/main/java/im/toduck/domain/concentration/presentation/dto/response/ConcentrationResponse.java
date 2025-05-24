package im.toduck.domain.concentration.presentation.dto.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "집중 응답")
@Builder
public record ConcentrationResponse(
	@Schema(description = "집중 ID", example = "1")
	Long id,

	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Schema(description = "집중 날짜", example = "2025-03-12")
	LocalDate date,

	@Schema(description = "달성 횟수", example = "2")
	Integer targetCount,

	@Schema(description = "설정 횟수", example = "5")
	Integer settingCount,

	@Schema(description = "집중 시간(초)", example = "1200")
	Integer time,

	@Schema(description = "달성률(%)", example = "40")
	Integer percentage
) {

}
