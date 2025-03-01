package im.toduck.domain.schedule.presentation.dto.request;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ScheduleCompleteRequest(
	@Schema(description = "일정 Id", example = "1")
	Long scheduleId,
	@Schema(description = "일정 완료 여부", example = "false")
	Boolean isComplete,
	@Schema(description = "일정 조회 날짜", example = "2024-08-31")
	LocalDate queryDate
) {
}
