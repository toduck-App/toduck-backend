package im.toduck.domain.schedule.domain.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.schedule.common.mapper.ScheduleMapper;
import im.toduck.domain.schedule.persistence.entity.Schedule;
import im.toduck.domain.schedule.persistence.repository.ScheduleRepository;
import im.toduck.domain.schedule.presentation.dto.request.ScheduleCreateRequest;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleCreateResponse;
import im.toduck.domain.user.persistence.entity.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {
	private final ScheduleRepository scheduleRepository;

	@Transactional
	public ScheduleCreateResponse createSchedule(User user, ScheduleCreateRequest request) {
		Schedule schedule = ScheduleMapper.toSchedule(user, request);
		Schedule save = scheduleRepository.save(schedule);
		return ScheduleMapper.toScheduleCreateResponse(save);
	}
}
