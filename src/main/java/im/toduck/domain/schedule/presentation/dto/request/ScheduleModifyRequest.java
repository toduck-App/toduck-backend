package im.toduck.domain.schedule.presentation.dto.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "일정 수정 요청 DTO")
public record ScheduleModifyRequest(
	@Schema(description = "해당 일정의 모 일정 ID", example = "1")
	Long scheduleId,

	@Schema(description = "하루 일정 혹은 이후 일정 수정 여부", example = "true")
	Boolean isOneDayDeleted,

	@Schema(description = "일정 수정 날짜", example = "2025-02-07")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonFormat(pattern = "yyyy-MM-dd")
	LocalDate queryDate,

	@Schema(description = "일정 수정 데이터 ")
	ScheduleCreateRequest scheduleData // TODO : 추후 분리해야한다면 분리

) {
}
