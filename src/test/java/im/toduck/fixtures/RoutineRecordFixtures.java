package im.toduck.fixtures;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;

import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.entity.RoutineRecord;

public class RoutineRecordFixtures {

	private static final Random random = new Random();

	public static RoutineRecord COMPLETED_SYNCED_RECORD(Routine routine) {
		return createRoutineRecord(routine, true, false, 0L);
	}

	public static RoutineRecord INCOMPLETED_SYNCED_RECORD(Routine routine) {
		return createRoutineRecord(routine, false, false, 0L);
	}

	public static RoutineRecord COMPLETED_MODIFIED_RECORD(Routine routine) {
		return createRoutineRecord(routine, true, true, 0L);
	}

	public static RoutineRecord INCOMPLETED_MODIFIED_RECORD(Routine routine) {
		return createRoutineRecord(routine, false, true, 0L);
	}

	public static RoutineRecord OFFSET_COMPLETED_SYNCED_RECORD(Routine routine, Long weekOffset) {
		return createRoutineRecord(routine, true, false, weekOffset);
	}

	public static RoutineRecord OFFSET_INCOMPLETED_SYNCED_RECORD(Routine routine, Long weekOffset) {
		return createRoutineRecord(routine, false, false, weekOffset);
	}

	private static RoutineRecord createRoutineRecord(
		Routine routine,
		boolean isCompleted,
		boolean isModified,
		Long weekOffset
	) {
		LocalDateTime recordedAt =
			isModified ? calculateModifiedRecordAt(routine, weekOffset) : calculateSyncedRecordAt(routine, weekOffset);

		return RoutineRecord.builder()
			.routine(routine)
			.recordAt(recordedAt)
			.isAllDay(routine.getTime() == null)
			.isCompleted(isCompleted)
			.build();
	}

	private static LocalDateTime calculateSyncedRecordAt(Routine routine, Long weekOffset) {
		return calculateRecordAt(routine, routine.getTime(), true, weekOffset);
	}

	private static LocalDateTime calculateModifiedRecordAt(Routine routine, Long weekOffset) {
		return calculateRecordAt(routine, generateDifferentTime(routine.getTime()), false, weekOffset);
	}

	private static LocalDateTime calculateRecordAt(
		Routine routine, LocalTime time,
		boolean shouldIncludeDay,
		long weekOffset
	) {
		LocalDate startDate = routine.getCreatedAt().toLocalDate();
		LocalTime routineTime = time != null ? time : LocalTime.MIDNIGHT;

		startDate = startDate.plusDays(weekOffset * 7);

		while (true) {
			if (routine.getDaysOfWeekBitmask().includesDayOf(startDate) == shouldIncludeDay) {
				return LocalDateTime.of(startDate, routineTime);
			}

			startDate = startDate.plusDays(1);
		}
	}

	private static LocalTime generateDifferentTime(LocalTime originalTime) {
		LocalTime newTime;
		do {
			int hour = random.nextInt(24);
			int minute = random.nextInt(60);
			newTime = LocalTime.of(hour, minute);
		} while (newTime.equals(originalTime));

		return newTime;
	}
}
