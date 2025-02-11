package im.toduck.domain.schedule.domain.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.schedule.common.mapper.ScheduleMapper;
import im.toduck.domain.schedule.persistence.entity.Schedule;
import im.toduck.domain.schedule.persistence.entity.ScheduleRecord;
import im.toduck.domain.schedule.persistence.repository.ScheduleRecordRepository;
import im.toduck.domain.schedule.persistence.repository.ScheduleRepository;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleCreateRequest;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleCreateResponse;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleHeadResponse;
import im.toduck.domain.user.persistence.entity.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {
	private final ScheduleRepository scheduleRepository;
	private final ScheduleRecordRepository scheduleRecordRepository;

	@Transactional
	public ScheduleCreateResponse createSchedule(User user, ScheduleCreateRequest request) {
		Schedule schedule = ScheduleMapper.toSchedule(user, request);
		Schedule save = scheduleRepository.save(schedule);
		return ScheduleMapper.toScheduleCreateResponse(save);
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
}
