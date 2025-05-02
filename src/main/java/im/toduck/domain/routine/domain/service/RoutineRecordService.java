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

	@Transactional(readOnly = true)
	public List<RoutineRecord> getRecordsIncludingDeleted(final User user, final LocalDate date) {
		return routineRecordRepository.findAllByUserAndRecordAtDate(user, date);
	}

	@Transactional(readOnly = true)
	public List<RoutineRecord> getRecordsBetweenDates(
		final User user,
		final LocalDate startDate,
		final LocalDate endDate
	) {
		return routineRecordRepository.findAllByUserAndRecordAtBetween(user, startDate, endDate);
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
	public void createAsDeleted(
		final Routine routine,
		final LocalDate date
	) {
		RoutineRecord routineRecord = RoutineRecordMapper.toRoutineRecord(routine, date, false);
		routineRecord.delete();
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

	@Transactional
	public boolean removeIfPresent(
		final Routine routine,
		final LocalDate date
	) {
		return routineRecordRepository.findByRoutineAndRecordDate(routine, date)
			.map(record -> {
				record.delete();
				return true;
			})
			.orElse(false);
	}

	@Transactional
	public void removeIncompletedFuturesByRoutine(final Routine routine, final LocalDateTime targetDateTime) {
		routineRecordRepository.deleteIncompletedFuturesByRoutine(routine, targetDateTime);
	}

	@Transactional
	public void removeAllByRoutine(final Routine routine) {
		routineRecordRepository.deleteAllByRoutine(routine);
	}

	@Transactional(readOnly = true)
	public Set<LocalDate> getExistingRecordDatesIncludingDeleted(
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
