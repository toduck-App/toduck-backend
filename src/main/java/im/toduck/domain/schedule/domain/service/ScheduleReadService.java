package im.toduck.domain.schedule.domain.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.schedule.common.mapper.ScheduleMapper;
import im.toduck.domain.schedule.persistence.entity.Schedule;
import im.toduck.domain.schedule.persistence.entity.ScheduleRecord;
import im.toduck.domain.schedule.persistence.repository.ScheduleRecordRepository;
import im.toduck.domain.schedule.persistence.repository.ScheduleRepository;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleHeadResponse;
import im.toduck.domain.schedule.presentation.dto.response.ScheduleInfoResponse;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.persistence.projection.DailyCount;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleReadService {
	private final ScheduleRepository scheduleRepository;
	private final ScheduleRecordRepository scheduleRecordRepository;

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

	public Schedule validateScheduleById(Long scheduleId) {
		return scheduleRepository.findById(scheduleId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_SCHEDULE));
	}

	@Transactional(readOnly = true)
	public long getSchedulesCountByDate(final LocalDate date) {
		LocalDateTime startDateTime = date.atStartOfDay();
		LocalDateTime endDateTime = date.atTime(LocalTime.MAX);
		return scheduleRepository.countByCreatedAtBetween(startDateTime, endDateTime);
	}

	@Transactional(readOnly = true)
	public long getTotalSchedulesCount() {
		return scheduleRepository.count();
	}

	@Transactional(readOnly = true)
	public long getSchedulesCountByDateRange(final LocalDate startDate, final LocalDate endDate) {
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
		return scheduleRepository.countByCreatedAtBetween(startDateTime, endDateTime);
	}

	@Transactional(readOnly = true)
	public Map<LocalDate, Long> getSchedulesCountByDateRangeGroupByDate(
		final LocalDate startDate,
		final LocalDate endDate
	) {
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

		List<DailyCount> dailyCounts = scheduleRepository.countByCreatedAtBetweenGroupByDate(
			startDateTime, endDateTime
		);

		return dailyCounts.stream().collect(Collectors.toMap(DailyCount::date, DailyCount::count));
	}
}
