package im.toduck.domain.routine.domain.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

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
}
