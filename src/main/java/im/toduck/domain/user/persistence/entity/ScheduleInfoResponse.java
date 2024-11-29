package im.toduck.domain.user.persistence.entity;

import lombok.Builder;

@Builder
public record ScheduleInfoResponse(
	Long scheduleId
) {
}
