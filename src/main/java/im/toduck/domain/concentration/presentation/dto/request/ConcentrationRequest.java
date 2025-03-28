package im.toduck.domain.concentration.presentation.dto.request;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ConcentrationRequest(
	@NotNull(message = "날짜는 필수 입력 항목입니다.")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@Schema(description = "집중 날짜", example = "2025-03-12")
	LocalDate date,

	@NotNull(message = "달성횟수는 필수 입력 항목입니다.")
	@Schema(description = "달성 횟수", example = "2")
	Integer targetCount,

	@NotNull(message = "설정횟수는 필수 입력 항목입니다.")
	@Schema(description = "설정 횟수", example = "5")
	Integer settingCount,

	@NotNull(message = "집중시간은 필수 입력 항목입니다.")
	@Schema(description = "집중 시간(초)", example = "1200")
	Integer time
) {
}
