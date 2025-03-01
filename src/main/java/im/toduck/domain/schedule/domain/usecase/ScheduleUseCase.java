package im.toduck.domain.schedule.domain.usecase;

import java.time.LocalDate;

import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.schedule.domain.service.ScheduleRecordService;
import im.toduck.domain.schedule.domain.service.ScheduleService;
import im.toduck.domain.schedule.persistence.entity.Schedule;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleCompleteRequest;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleCreateRequest;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleDeleteRequest;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleCreateResponse;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleHeadResponse;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleInfoResponse;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.annotation.UseCase;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@UseCase
@RequiredArgsConstructor
@Slf4j
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

	@Transactional
	public void deleteSchedule(Long userId, ScheduleDeleteRequest scheduleDeleteRequest) {
		userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));
		Schedule schedule = scheduleService.getScheduleById(scheduleDeleteRequest.scheduleId())
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_SCHEDULE));

		if (isSingleDaySchedule(schedule)) {
			scheduleService.deleteSingleDaySchedule(schedule, scheduleDeleteRequest);
			log.info("반복 X 하루 일정 삭제 성공 : {}", scheduleDeleteRequest.scheduleId());
			return;
		}
		if (scheduleDeleteRequest.isOneDayDeleted()) {
			scheduleService.deleteOneDayDeletionForRepeatingSchedule(schedule, scheduleDeleteRequest);
			log.info("반복 일정 중 하루 삭제 성공 : {}", scheduleDeleteRequest.scheduleId());
			return;
		}
		scheduleService.deleteAfterDeletionForRepeatingSchedule(schedule, scheduleDeleteRequest);
		log.info("반복 일정 중 기간 삭제 성공 : {}", scheduleDeleteRequest.scheduleId());
	}

	private boolean isSingleDaySchedule(Schedule schedule) {
		return schedule.getScheduleDate().getStartDate().equals(schedule.getScheduleDate().getEndDate())
			&& schedule.getDaysOfWeekBitmask() == null;
	}

}
