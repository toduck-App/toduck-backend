package im.toduck.domain.schedule.domain.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.schedule.common.mapper.ScheduleMapper;
import im.toduck.domain.schedule.common.mapper.ScheduleRecordMapper;
import im.toduck.domain.schedule.persistence.entity.Schedule;
import im.toduck.domain.schedule.persistence.entity.ScheduleRecord;
import im.toduck.domain.schedule.persistence.repository.ScheduleRecordRepository;
import im.toduck.domain.schedule.persistence.repository.ScheduleRepository;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleDeleteRequest;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleModifyRequest;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleIdResponse;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleModifyService {
	private final ScheduleRepository scheduleRepository;
	private final ScheduleRecordRepository scheduleRecordRepository;

	public void deleteSingleDaySchedule(Schedule schedule, ScheduleDeleteRequest request) {
		if (!request.isOneDayDeleted()) {
			throw CommonException.from(ExceptionCode.NON_REPESTITIVE_ONE_SCHEDULE_NOT_PERIOD_DELETE);
		}
		scheduleRecordRepository.deleteByScheduleIdAndRecordDate(
			schedule.getId(),
			schedule.getScheduleDate().getStartDate());
		scheduleRepository.delete(schedule);
	}

	public void deleteOneDayDeletionForRepeatingSchedule(Schedule schedule, ScheduleDeleteRequest request) {
		softDeleteScheduleRecord(request.queryDate(), schedule);
	}

	@Transactional
	public void deleteAfterDeletionForRepeatingSchedule(Schedule schedule,
		ScheduleDeleteRequest scheduleDeleteRequest) {

		// 특정 날짜의 일정 기록이 있는지 확인하고 있으면 soft delete, 없으면 soft delete된 일정 기록 생성
		deleteScheduleRecord(schedule, scheduleDeleteRequest.queryDate());

		if (schedule.getScheduleDate().getStartDate().equals(scheduleDeleteRequest.queryDate())) {
			scheduleRepository.delete(schedule);
			return;
		}
		schedule.changeEndDate(scheduleDeleteRequest.queryDate().minusDays(1));
	}

	// 특정 날짜 이후 기록 중 완료 기록이 있다면 각각 하루짜리 반복 없는 일정 기록으로 변경
	private void deleteScheduleRecord(Schedule schedule, LocalDate scheduleDeleteRequest) {
		scheduleRecordRepository.findByCompletedScheduleAndAfterStartDate(
				schedule.getId(),
				scheduleDeleteRequest)
			.forEach(scheduleRecord -> {
				Schedule save = scheduleRepository.save(
					ScheduleMapper.copyToSchedule(schedule, scheduleDeleteRequest));
				scheduleRecord.changeSchedule(save);
			});
		scheduleRecordRepository.deleteByNonCompletedScheduleAndAfterStartDate(
			schedule.getId(),
			scheduleDeleteRequest,
			schedule.getScheduleDate().getEndDate());
	}

	@Transactional
	public ScheduleIdResponse updateSingleDate(Schedule schedule, ScheduleModifyRequest request) {
		if (isSingleDaySchedule(schedule)) {
			schedule.updateInfo(request.scheduleData());
			return ScheduleMapper.toScheduleIdResponse(schedule);
		}
		softDeleteScheduleRecord(request.queryDate(), schedule);
		Schedule newSchedule = ScheduleMapper.toSchedule(schedule.getUser(), request.scheduleData());
		return ScheduleMapper
			.toScheduleIdResponse(scheduleRepository.save(newSchedule));
	}

	// 특정 날짜의 일정 기록이 있는지 확인하고 있으면 soft delete, 없으면 soft delete된 일정 기록 생성
	private void softDeleteScheduleRecord(LocalDate requestQueryDate, Schedule schedule) {
		scheduleRecordRepository.findScheduleRecordByRecordDateAndScheduleId(
				requestQueryDate,
				schedule.getId())
			.ifPresentOrElse(scheduleRecord -> {
				scheduleRecordRepository.softDeleteByScheduleIdAndRecordDate(
					schedule.getId(),
					requestQueryDate);
			}, () -> {
				ScheduleRecord softDeletedScheduleRecord = ScheduleRecordMapper
					.toSoftDeletedScheduleRecord(schedule, requestQueryDate);
				scheduleRecordRepository.save(softDeletedScheduleRecord);
			});
	}

	private boolean isSingleDaySchedule(Schedule schedule) {
		return schedule.getScheduleDate().getStartDate().equals(schedule.getScheduleDate().getEndDate())
			&& schedule.getDaysOfWeekBitmask() == null;
	}

	@Transactional
	public ScheduleIdResponse updateAfterDate(Schedule schedule, ScheduleModifyRequest request) {
		// 특정 날짜 이후 기록 중 완료 기록이 있다면 각각 하루짜리 반복 없는 일정 기록으로 변경
		deleteScheduleRecord(schedule, request.queryDate());
		// 특정 날짜가 시작일이라면 해당 일정 삭제
		if (schedule.getScheduleDate().getStartDate().equals(request.queryDate())) {
			scheduleRepository.delete(schedule);
		} else {
			// 특정 날짜가 시작일이 아니라면 종료일을 특정 날짜 하루 전으로 변경
			schedule.changeEndDate(request.queryDate().minusDays(1));
		}

		// 새로운 일정 생성
		Schedule newSchedule = ScheduleMapper.toSchedule(schedule.getUser(), request.scheduleData());
		return ScheduleMapper
			.toScheduleIdResponse(scheduleRepository.save(newSchedule));
	}
}
