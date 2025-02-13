package im.toduck.domain.schedule.domain.usecase;

import java.time.LocalDate;

import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.schedule.domain.service.ScheduleRecordService;
import im.toduck.domain.schedule.domain.service.ScheduleService;
import im.toduck.domain.schedule.persistence.entity.Schedule;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleCompleteRequest;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleCreateRequest;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleCreateResponse;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleHeadResponse;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleInfoResponse;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.annotation.UseCase;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class ScheduleUseCase {
	private final ScheduleService scheduleService;
	private final UserService userService;
	private final ScheduleRecordService scheduleRecordService;

	@Transactional
	public ScheduleCreateResponse createSchedule(Long userId,
		ScheduleCreateRequest request) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		return scheduleService.createSchedule(user, request);
	}

	@Transactional(readOnly = true)
	public ScheduleHeadResponse getRangeSchedule(Long userId, LocalDate startDate, LocalDate endDate) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		return scheduleService.getRangeSchedule(user, startDate, endDate);
	}

	@Transactional(readOnly = true)
	public ScheduleInfoResponse getSchedule(Long userId, Long scheduleRecordId) {
		userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		return scheduleService.getSchedule(scheduleRecordId);
	}

	@Transactional
	public void completeSchedule(Long userId, ScheduleCompleteRequest scheduleCompleteRequest) {
		userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Schedule schedule = scheduleService.getScheduleById(scheduleCompleteRequest.scheduleId())
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_SCHEDULE));
		scheduleRecordService.getScheduleRecordWithSchedule(userId, scheduleCompleteRequest)
			.ifPresentOrElse((scheduleRecord) -> {
				scheduleRecordService.completeScheduleRecord(scheduleRecord, scheduleCompleteRequest);
			}, () -> {
				scheduleRecordService.createScheduleRecord(schedule, scheduleCompleteRequest);
			});
	}
}
