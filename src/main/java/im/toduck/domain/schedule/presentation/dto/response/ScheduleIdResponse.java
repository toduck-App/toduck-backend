package im.toduck.domain.schedule.presentation.dto.response;

import im.toduck.domain.schedule.persistence.entity.Schedule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "일정 생성 응답 DTO")
@Builder
public record ScheduleIdResponse(
	@Schema(description = "생성된 일정 Id", example = "1")
	Long scheduleId
) {
	public static ScheduleIdResponse of(Schedule schedule) {
		return ScheduleIdResponse.builder()
			.scheduleId(schedule.getId())
			.build();
	}
}
