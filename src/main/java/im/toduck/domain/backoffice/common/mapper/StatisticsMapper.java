package im.toduck.domain.backoffice.common.mapper;

import java.time.LocalDate;

import im.toduck.domain.backoffice.presentation.dto.response.DailyStatisticsResponse;
import im.toduck.domain.backoffice.presentation.dto.response.OverallStatisticsResponse;
import im.toduck.domain.backoffice.presentation.dto.response.PeriodStatisticsResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StatisticsMapper {

	public static OverallStatisticsResponse toOverallStatisticsResponse(
		final long totalUserCount,
		final long totalDiaryCount,
		final long totalRoutineCount,
		final long activeDiaryWritersCount,
		final long activeRoutineUsersCount
	) {
		return OverallStatisticsResponse.builder()
			.totalUserCount(totalUserCount)
			.totalDiaryCount(totalDiaryCount)
			.totalRoutineCount(totalRoutineCount)
			.activeDiaryWritersCount(activeDiaryWritersCount)
			.activeRoutineUsersCount(activeRoutineUsersCount)
			.build();
	}

	public static PeriodStatisticsResponse toPeriodStatisticsResponse(
		final long newUsersCount,
		final long deletedUsersCount,
		final long newDiariesCount,
		final long newRoutinesCount,
		final LocalDate startDate,
		final LocalDate endDate
	) {
		return PeriodStatisticsResponse.builder()
			.newUsersCount(newUsersCount)
			.deletedUsersCount(deletedUsersCount)
			.newDiariesCount(newDiariesCount)
			.newRoutinesCount(newRoutinesCount)
			.startDate(startDate)
			.endDate(endDate)
			.build();
	}

	public static DailyStatisticsResponse toDailyStatisticsResponse(
		final long newUsersCount,
		final long deletedUsersCount,
		final long newDiariesCount,
		final long newRoutinesCount,
		final LocalDate date
	) {
		return DailyStatisticsResponse.builder()
			.newUsersCount(newUsersCount)
			.deletedUsersCount(deletedUsersCount)
			.newDiariesCount(newDiariesCount)
			.newRoutinesCount(newRoutinesCount)
			.date(date)
			.build();
	}
}
