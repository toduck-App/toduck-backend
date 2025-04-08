package im.toduck.domain.schedule.presentation.dto.request;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ScheduleDeleteRequest(
	@Schema(description = "일정 Id", example = "1")
	@NotNull
	Long scheduleId,
	@Schema(description = "일정 하루 삭제 OR 이후 삭제 여부 ", example = "true")
	@NotNull
	Boolean isOneDayDeleted,
	@Schema(description = "일정 삭제 날짜", example = "2024-08-31")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@NotNull
	LocalDate queryDate
) {
}
