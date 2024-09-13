package im.toduck.domain.routine.common.mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.entity.RoutineRecord;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class RoutineRecordMapper {
	public static RoutineRecord toRoutineRecord(
		final Routine routine,
		final LocalDate date,
		final boolean isCompleted
	) {
		boolean isAllDay = routine.getTime() == null;
		LocalTime recordDate = isAllDay ? LocalTime.MIDNIGHT : routine.getTime();
		LocalDateTime recordAt = LocalDateTime.of(date, recordDate);

		return RoutineRecord.builder()
			.routine(routine)
			.recordAt(recordAt)
			.isAllDay(isAllDay)
			.isCompleted(isCompleted)
			.build();
	}
}
