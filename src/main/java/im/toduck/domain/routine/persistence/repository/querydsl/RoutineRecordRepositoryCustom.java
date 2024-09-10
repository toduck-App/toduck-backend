package im.toduck.domain.routine.persistence.repository.querydsl;

import java.time.LocalDate;
import java.util.List;

import im.toduck.domain.routine.persistence.entity.RoutineRecord;
import im.toduck.domain.user.persistence.entity.User;

public interface RoutineRecordRepositoryCustom {
	List<RoutineRecord> findRoutineRecordsForUserAndDate(User user, LocalDate date);
}
