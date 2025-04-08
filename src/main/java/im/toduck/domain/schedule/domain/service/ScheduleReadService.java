package im.toduck.domain.schedule.domain.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.schedule.common.mapper.ScheduleMapper;
import im.toduck.domain.schedule.persistence.entity.Schedule;
import im.toduck.domain.schedule.persistence.entity.ScheduleRecord;
import im.toduck.domain.schedule.persistence.repository.ScheduleRecordRepository;
import im.toduck.domain.schedule.persistence.repository.ScheduleRepository;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleCreateRequest;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleHeadResponse;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleIdResponse;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleInfoResponse;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleReadService {
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
}
