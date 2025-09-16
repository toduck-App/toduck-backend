package im.toduck.domain.backoffice.common.mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import im.toduck.domain.backoffice.presentation.dto.request.StatisticsType;
import im.toduck.domain.backoffice.presentation.dto.response.DailyStatisticsData;
import im.toduck.domain.backoffice.presentation.dto.response.MultiDateStatisticsResponse;
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

	public static DailyStatisticsData toDailyStatisticsData(
		final LocalDate date,
		final Map<StatisticsType, Long> counts
	) {
		return DailyStatisticsData.builder()
			.date(date)
			.counts(counts)
			.build();
	}

	public static MultiDateStatisticsResponse toMultiDateStatisticsResponse(
		final List<DailyStatisticsData> statisticsDataList,
		final LocalDate startDate,
		final LocalDate endDate
	) {
		return MultiDateStatisticsResponse.builder()
			.statistics(statisticsDataList)
			.startDate(startDate)
			.endDate(endDate)
			.build();
	}
}
