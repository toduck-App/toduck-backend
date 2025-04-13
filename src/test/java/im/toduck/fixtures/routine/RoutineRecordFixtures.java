package im.toduck.fixtures.routine;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.entity.RoutineRecord;

public class RoutineRecordFixtures {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public static RoutineRecordBuilder COMPLETED_RECORD(Routine routine) {
		return new RoutineRecordBuilder(routine, true);
	}

	public static RoutineRecordBuilder INCOMPLETED_RECORD(Routine routine) {
		return new RoutineRecordBuilder(routine, false);
	}

	public static class RoutineRecordBuilder {
		private final Routine routine;
		private final boolean isCompleted;
		private LocalDateTime recordAt;
		private boolean isAllDay;

		private RoutineRecordBuilder(Routine routine, boolean isCompleted) {
			this.routine = routine;
			this.isCompleted = isCompleted;
		}

		public RoutineRecordBuilder recordAt(String recordAt) {
			this.recordAt = LocalDateTime.parse(recordAt, FORMATTER);
			return this;
		}

		public RoutineRecordBuilder allDay(boolean isAllDay) {
			this.isAllDay = isAllDay;
			return this;
		}

		public RoutineRecord build() {
			if (recordAt == null) {
				throw new IllegalStateException("recordAt을 설정하세요");
			}

			return RoutineRecord.builder()
				.routine(routine)
				.recordAt(recordAt)
				.isAllDay(isAllDay)
				.isCompleted(isCompleted)
				.build();
		}
	}
}
