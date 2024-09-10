package im.toduck.domain.routine.persistence.repository.querydsl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import im.toduck.domain.routine.persistence.entity.QRoutine;
import im.toduck.domain.routine.persistence.entity.QRoutineRecord;
import im.toduck.domain.routine.persistence.entity.RoutineRecord;
import im.toduck.domain.user.persistence.entity.User;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RoutineRecordRepositoryCustomImpl implements RoutineRecordRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<RoutineRecord> findRoutineRecordsForUserAndDate(User user, LocalDate date) {
		QRoutine routine = QRoutine.routine;
		QRoutineRecord record = QRoutineRecord.routineRecord;

		LocalDateTime startOfDay = date.atStartOfDay();
		LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

		return queryFactory
			.selectFrom(record)
			.join(record.routine, routine)
			.where(
				record.recordAt.between(startOfDay, endOfDay)
			)
			.fetch();
	}
}
