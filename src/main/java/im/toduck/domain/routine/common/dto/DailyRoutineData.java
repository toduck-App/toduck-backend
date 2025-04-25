package im.toduck.domain.routine.common.dto;

import java.time.LocalDate;
import java.util.List;

import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.entity.RoutineRecord;

public record DailyRoutineData(
	LocalDate date,
	List<Routine> routines,
	List<RoutineRecord> routineRecords
) {
	public static DailyRoutineData of(LocalDate date, List<Routine> routines, List<RoutineRecord> routineRecords) {
		return new DailyRoutineData(date, routines, routineRecords);
	}
}
