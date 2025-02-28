package im.toduck.domain.schedule.domain.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.schedule.common.mapper.ScheduleMapper;
import im.toduck.domain.schedule.common.mapper.ScheduleRecordMapper;
import im.toduck.domain.schedule.persistence.entity.Schedule;
import im.toduck.domain.schedule.persistence.entity.ScheduleRecord;
import im.toduck.domain.schedule.persistence.repository.ScheduleRecordRepository;
import im.toduck.domain.schedule.persistence.repository.ScheduleRepository;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleCreateRequest;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleDeleteRequest;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleModifyRequest;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleHeadResponse;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleIdResponse;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleInfoResponse;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {
	private final ScheduleRepository scheduleRepository;
	private final ScheduleRecordRepository scheduleRecordRepository;

	@Transactional
	public ScheduleIdResponse createSchedule(User user, ScheduleCreateRequest request) {
		Schedule schedule = ScheduleMapper.toSchedule(user, request);
		Schedule save = scheduleRepository.save(schedule);
		return ScheduleMapper.toScheduleIdResponse(save);
	}

	@Transactional(readOnly = true)
	public ScheduleHeadResponse getRangeSchedule(User user, LocalDate startDate, LocalDate endDate) {
		List<ScheduleHeadResponse.ScheduleHeadDto> scheduleHeadDtos = new ArrayList<>();
		scheduleRepository.findSchedules(user.getId(), startDate, endDate)
			.forEach(schedule -> {
				List<ScheduleRecord> scheduleRecordList = scheduleRecordRepository
					.findByScheduleAndBetweenStartDateAndEndDate(schedule.getId(), startDate, endDate);
				scheduleHeadDtos.add(ScheduleMapper.toScheduleHeadDto(schedule, scheduleRecordList));
			});
		return ScheduleMapper.toScheduleHeadResponse(startDate, endDate, scheduleHeadDtos);
	}

	@Transactional(readOnly = true)
	public ScheduleInfoResponse getSchedule(Long scheduleRecordId) {
		return scheduleRecordRepository.findScheduleRecordFetchJoinSchedule(scheduleRecordId)
			.map(ScheduleMapper::toScheduleInfoResponse)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_SCHEDULE_RECORD));
	}

	public Optional<Schedule> getScheduleById(Long scheduleId) {
		return scheduleRepository.findById(scheduleId);
	}

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
		scheduleRecordRepository.findScheduleRecordByUserIdAndRecordDateAndScheduleId(
				request.queryDate(),
				schedule.getId())
			.ifPresentOrElse(scheduleRecord -> {
				scheduleRecordRepository.softDeleteByScheduleIdAndRecordDate(
					schedule.getId(),
					request.queryDate());
			}, () -> {
				ScheduleRecord softDeletedScheduleRecord = ScheduleRecordMapper
					.toSoftDeletedScheduleRecord(schedule, request.queryDate());
				scheduleRecordRepository.save(softDeletedScheduleRecord);
			});
	}

	@Transactional
	public void deleteAfterDeletionForRepeatingSchedule(Schedule schedule,
		ScheduleDeleteRequest scheduleDeleteRequest) {
		scheduleRecordRepository.findByCompletedScheduleAndAfterStartDate(
				schedule.getId(),
				scheduleDeleteRequest.queryDate())
			.forEach(scheduleRecord -> {
				Schedule save = scheduleRepository.save(
					ScheduleMapper.copyToSchedule(schedule, scheduleDeleteRequest.queryDate()));
				scheduleRecord.changeSchedule(save);
			});
		scheduleRecordRepository.deleteByNonCompletedScheduleAndAfterStartDate(
			schedule.getId(),
			scheduleDeleteRequest.queryDate(),
			schedule.getScheduleDate().getEndDate());
		if (schedule.getScheduleDate().getStartDate().equals(scheduleDeleteRequest.queryDate())) {
			scheduleRepository.delete(schedule);
			return;
		}
		schedule.changeEndDate(scheduleDeleteRequest.queryDate().minusDays(1));
	}

	public ScheduleIdResponse updateSingleDate(Schedule schedule, ScheduleModifyRequest request) {
		if (isSingleDaySchedule(schedule)) {
			schedule.updateInfo(request.scheduleData());
			return ScheduleMapper.toScheduleIdResponse(schedule);
		}
		// 특정 날짜의 일정 기록이 있는지 확인하고 있으면 soft delete, 없으면 soft delete된 일정 기록 생성
		scheduleRecordRepository.findScheduleRecordByUserIdAndRecordDateAndScheduleId(
				request.queryDate(),
				schedule.getId())
			.ifPresentOrElse(scheduleRecord -> {
				scheduleRecordRepository.softDeleteByScheduleIdAndRecordDate(
					schedule.getId(),
					request.queryDate());
			}, () -> {
				ScheduleRecord softDeletedScheduleRecord = ScheduleRecordMapper
					.toSoftDeletedScheduleRecord(schedule, request.queryDate());
				scheduleRecordRepository.save(softDeletedScheduleRecord);
			});
		Schedule newSchedule = ScheduleMapper.toSchedule(schedule.getUser(), request.scheduleData());
		return ScheduleMapper
			.toScheduleIdResponse(scheduleRepository.save(newSchedule));
	}

	private boolean isSingleDaySchedule(Schedule schedule) {
		return schedule.getScheduleDate().getStartDate().equals(schedule.getScheduleDate().getEndDate())
			&& schedule.getDaysOfWeekBitmask() == null;
	}

	public ScheduleIdResponse updateAfterDate(Schedule schedule, ScheduleModifyRequest request) {
		// 특정 날짜 이후 기록 중 완료 기록이 있다면 각각 하루짜리 반복 없는 일정 기록으로 변경
		scheduleRecordRepository.findByCompletedScheduleAndAfterStartDate(
				schedule.getId(),
				request.queryDate())
			.forEach(scheduleRecord -> {
				Schedule save = scheduleRepository.save(
					ScheduleMapper.copyToSchedule(schedule, request.queryDate()));
				scheduleRecord.changeSchedule(save);
			});
		// 특정 날짜 이후 기록 중 미완료 기록이 있다면 삭제
		scheduleRecordRepository.deleteByNonCompletedScheduleAndAfterStartDate(
			schedule.getId(),
			request.queryDate(),
			schedule.getScheduleDate().getEndDate());
		// 특정 날짜가 시작일이라면 해당 일정 삭제
		if (schedule.getScheduleDate().getStartDate().equals(request.queryDate())) {
			scheduleRepository.delete(schedule);
		}
		// 특정 날짜가 시작일이 아니라면 종료일을 특정 날짜 하루 전으로 변경
		schedule.changeEndDate(request.queryDate().minusDays(1));

		// 새로운 일정 생성
		Schedule newSchedule = ScheduleMapper.toSchedule(schedule.getUser(), request.scheduleData());
		return ScheduleMapper
			.toScheduleIdResponse(scheduleRepository.save(newSchedule));
	}
}
