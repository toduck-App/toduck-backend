package im.toduck.domain.routine.domain.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.routine.common.mapper.RoutineRecordMapper;
import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.entity.RoutineRecord;
import im.toduck.domain.routine.persistence.repository.RoutineRecordRepository;
import im.toduck.domain.user.persistence.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoutineRecordService {
	private final RoutineRecordRepository routineRecordRepository;

	public List<RoutineRecord> getRecords(final User user, final LocalDate date) {
		return routineRecordRepository.findRoutineRecordsForUserAndDate(user, date);
	}

	@Transactional
	public void create(
		final Routine routine,
		final LocalDate date,
		final boolean isCompleted
	) {
		RoutineRecord routineRecord = RoutineRecordMapper.toRoutineRecord(routine, date, isCompleted);
		routineRecordRepository.save(routineRecord);
	}

	@Transactional
	public boolean updateIfPresent(
		final Routine routine,
		final LocalDate date,
		final boolean isCompleted
	) {
		return routineRecordRepository.findByRoutineAndRecordDate(routine, date)
			.map(record -> {
				record.changeCompletion(isCompleted);
				return true;
			})
			.orElse(false);
	}

	public void removeIncompletedFuturesByRoutine(final Routine routine, final LocalDateTime targetDateTime) {
		routineRecordRepository.deleteIncompletedFuturesByRoutine(routine, targetDateTime);
	}

	public void removeAllByRoutine(final Routine routine) {
		routineRecordRepository.deleteAllByRoutine(routine);
	}

	public Set<LocalDate> getExistingRecordDates(
		final Routine routine,
		final LocalDateTime startTime,
		final LocalDateTime endTime
	) {
		return routineRecordRepository.findAllByRoutineAndRecordAtBetween(routine, startTime, endTime)
			.stream()
			.map(record -> record.getRecordAt().toLocalDate())
			.collect(Collectors.toSet());
	}

	public void saveAll(final List<RoutineRecord> newRecords) {
		routineRecordRepository.saveAll(newRecords);
	}
}
