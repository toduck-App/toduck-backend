package im.toduck.domain.schedule.presentation.dto.response;

import lombok.Builder;

@Builder
public record ScheduleInfoResponse(
	Long scheduleId
) {
}
