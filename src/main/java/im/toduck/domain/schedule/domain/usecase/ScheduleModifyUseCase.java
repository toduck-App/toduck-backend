package im.toduck.domain.schedule.domain.usecase;

import java.time.LocalDate;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import im.toduck.domain.schedule.domain.service.ScheduleModifyService;
import im.toduck.domain.schedule.domain.service.ScheduleReadService;
import im.toduck.domain.schedule.persistence.entity.Schedule;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleCompleteRequest;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleCreateRequest;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleDeleteRequest;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleModifyRequest;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleHeadResponse;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleIdResponse;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleInfoResponse;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.annotation.UseCase;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@UseCase
@Validated
@RequiredArgsConstructor
@Slf4j
public class ScheduleModifyUseCase {
	private final ScheduleReadService scheduleReadService;
	private final UserService userService;
	private final ScheduleModifyService scheduleModifyService;

	@Transactional
	public ScheduleIdResponse createSchedule(final Long userId, final ScheduleCreateRequest request) {
		User user = userService.validateUserById(userId);

		Schedule schedule = Schedule.create(user, request);

		return ScheduleIdResponse.of(scheduleModifyService.save(schedule));
	}

	@Transactional(readOnly = true)
	public ScheduleHeadResponse getRangeSchedule(Long userId, LocalDate startDate, LocalDate endDate) {
		User user = userService.validateUserById(userId);

		return scheduleReadService.getRangeSchedule(user, startDate, endDate);
	}

	@Transactional(readOnly = true)
	public ScheduleInfoResponse getSchedule(Long userId, Long scheduleRecordId) {
		userService.validateUserById(userId);

		return scheduleReadService.getSchedule(scheduleRecordId);
	}

	@Transactional
	public void completeSchedule(Long userId, ScheduleCompleteRequest scheduleCompleteRequest) {
		userService.validateUserById(userId);

		Schedule schedule = scheduleReadService.validateScheduleById(scheduleCompleteRequest.scheduleId());

		schedule.completeSchedule(scheduleCompleteRequest);

		scheduleModifyService.save(schedule);
	}

	@Transactional
	public void deleteSchedule(Long userId, ScheduleDeleteRequest scheduleDeleteRequest) {
		userService.validateUserById(userId);

		Schedule schedule = scheduleReadService.getScheduleById(scheduleDeleteRequest.scheduleId())
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_SCHEDULE));

		if (isSingleDaySchedule(schedule)) {
			scheduleModifyService.deleteSingleDaySchedule(schedule, scheduleDeleteRequest);
			log.info("반복 X 하루 일정 삭제 성공 : {}", scheduleDeleteRequest.scheduleId());
			return;
		}
		if (scheduleDeleteRequest.isOneDayDeleted()) {
			scheduleModifyService.deleteOneDayDeletionForRepeatingSchedule(schedule, scheduleDeleteRequest);
			log.info("반복 일정 중 하루 삭제 성공 : {}", scheduleDeleteRequest.scheduleId());
			return;
		}
		scheduleModifyService.deleteAfterDeletionForRepeatingSchedule(schedule, scheduleDeleteRequest);
		log.info("반복 일정 중 기간 삭제 성공 : {}", scheduleDeleteRequest.scheduleId());
	}

	private boolean isSingleDaySchedule(Schedule schedule) {
		return schedule.getScheduleDate().getStartDate().equals(schedule.getScheduleDate().getEndDate())
			&& schedule.getDaysOfWeekBitmask() == null;
	}

	@Transactional
	public ScheduleIdResponse updateSchedule(Long userId, ScheduleModifyRequest request) {
		userService.validateUserById(userId);

		Schedule schedule = scheduleReadService.getScheduleById(request.scheduleId())
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_SCHEDULE));

		if (isSingleDaySchedule(schedule) && !request.isOneDayDeleted()) {
			log.info("반복 X 하루 일정은 하루 삭제만 가능 : {}", request.scheduleId());
			throw CommonException.from(ExceptionCode.ONE_DAY__NONREPEATABLE_SCHEDULE_CANNOT_AFTER_DATE_UPDATE);
		}
		if (!request.scheduleData().startDate().equals(request.scheduleData().endDate())
			&& !request.isOneDayDeleted()) {
			log.info("기간 일정으로 수정은 하루 삭제만 가능 scheduleId : {}", request.scheduleId());
			throw CommonException.from(ExceptionCode.PERIOD_SCHEDULE_CANNOT_AFTER_DATE_UPDATE);
		}
		if (request.isOneDayDeleted()) {
			log.info("하루의 일정만 수정 : {}", request.scheduleId());
			return scheduleModifyService.updateSingleDate(schedule, request);
		}
		log.info("특정 날짜 이후 일괄 일정 수정 : {}", request.scheduleId());
		return scheduleModifyService.updateAfterDate(schedule, request);
	}
}
