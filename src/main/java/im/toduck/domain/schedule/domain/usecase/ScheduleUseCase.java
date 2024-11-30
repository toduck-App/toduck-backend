package im.toduck.domain.schedule.domain.usecase;

import im.toduck.domain.schedule.presentation.dto.request.ScheduleCreateRequest;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleInfoResponse;
import im.toduck.global.annotation.UseCase;
import im.toduck.global.security.authentication.CustomUserDetails;

@UseCase
public class ScheduleUseCase {
	public ScheduleInfoResponse postSchedule(CustomUserDetails customUserDetails,
		ScheduleCreateRequest request) {
		return ScheduleInfoResponse.builder()
			.scheduleId(1L)
			.build();
	}
}
