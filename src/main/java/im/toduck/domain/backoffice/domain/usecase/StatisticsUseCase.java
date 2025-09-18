package im.toduck.domain.backoffice.domain.usecase;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.backoffice.common.mapper.StatisticsMapper;
import im.toduck.domain.backoffice.presentation.dto.request.StatisticsType;
import im.toduck.domain.backoffice.presentation.dto.response.DailyStatisticsData;
import im.toduck.domain.backoffice.presentation.dto.response.MultiDateStatisticsResponse;
import im.toduck.domain.backoffice.presentation.dto.response.OverallStatisticsResponse;
import im.toduck.domain.backoffice.presentation.dto.response.PeriodStatisticsResponse;
import im.toduck.domain.diary.domain.service.DiaryService;
import im.toduck.domain.routine.domain.service.RoutineService;
import im.toduck.domain.schedule.domain.service.ScheduleReadService;
import im.toduck.domain.social.domain.service.SocialBoardService;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.global.annotation.UseCase;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class StatisticsUseCase {
	private static final int MAX_STATISTICS_DATE_RANGE_DAYS = 31;

	private final UserService userService;
	private final DiaryService diaryService;
	private final RoutineService routineService;
	private final SocialBoardService socialBoardService;
	private final ScheduleReadService scheduleReadService;

	@Transactional(readOnly = true)
	public OverallStatisticsResponse getOverallStatistics(final List<StatisticsType> types) {
		Map<StatisticsType, Long> statisticsMap = new EnumMap<>(StatisticsType.class);

		for (StatisticsType type : types) {
			long count = getOverallStatisticsCountByType(type);
			statisticsMap.put(type, count);
		}

		log.info("백오피스 전체 통계 조회 - 조회 타입: {}", types);

		return StatisticsMapper.toOverallStatisticsResponse(statisticsMap);
	}

	@Transactional(readOnly = true)
	public PeriodStatisticsResponse getPeriodStatistics(
		final LocalDate startDate,
		final LocalDate endDate,
		final List<StatisticsType> types
	) {
		Map<StatisticsType, Long> statisticsMap = new EnumMap<>(StatisticsType.class);

		for (StatisticsType type : types) {
			long count = getStatisticsCountByTypeAndDateRange(type, startDate, endDate);
			statisticsMap.put(type, count);
		}

		log.info("백오피스 기간별 통계 조회 - 기간: {} ~ {}, 조회 타입: {}",
			startDate, endDate, types);

		return StatisticsMapper.toPeriodStatisticsResponse(
			statisticsMap, startDate, endDate
		);
	}

	@Transactional(readOnly = true)
	public MultiDateStatisticsResponse getMultiDateStatistics(
		final LocalDate startDate,
		final LocalDate endDate,
		final List<StatisticsType> types
	) {
		validateDateRange(startDate, endDate);

		List<DailyStatisticsData> statisticsDataList = new ArrayList<>();
		LocalDate currentDate = startDate;

		while (!currentDate.isAfter(endDate)) {
			Map<StatisticsType, Long> counts = new EnumMap<>(StatisticsType.class);

			for (StatisticsType type : types) {
				long count = getStatisticsCountByTypeAndDate(type, currentDate);
				counts.put(type, count);
			}

			statisticsDataList.add(StatisticsMapper.toDailyStatisticsData(currentDate, counts));
			currentDate = currentDate.plusDays(1);
		}

		log.info("백오피스 멀티 데이트 통계 조회 - 기간: {} ~ {}, 조회 타입: {}",
			startDate, endDate, types);

		return StatisticsMapper.toMultiDateStatisticsResponse(
			statisticsDataList, startDate, endDate
		);
	}

	private void validateDateRange(final LocalDate startDate, final LocalDate endDate) {
		if (startDate.isAfter(endDate)) {
			throw CommonException.from(ExceptionCode.INVALID_STATISTICS_DATE_RANGE);
		}

		long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
		if (daysBetween > MAX_STATISTICS_DATE_RANGE_DAYS) {
			throw CommonException.from(ExceptionCode.INVALID_STATISTICS_DATE_RANGE);
		}
	}

	private long getStatisticsCountByTypeAndDate(final StatisticsType type, final LocalDate date) {
		return switch (type) {
			case NEW_USERS -> userService.getNewUsersCountByDateRange(date, date);
			case DELETED_USERS -> userService.getDeletedUsersCountByDateRange(date, date);
			case NEW_ROUTINES -> routineService.getRoutineCountByDateRange(date, date);
			case NEW_DIARIES -> diaryService.getDiaryCountByDateRange(date, date);
			case NEW_SOCIAL_POSTS -> socialBoardService.getSocialPostsCountByDate(date);
			case NEW_COMMENTS -> socialBoardService.getCommentsCountByDate(date);
			case NEW_SCHEDULES -> scheduleReadService.getSchedulesCountByDate(date);
		};
	}

	private long getOverallStatisticsCountByType(final StatisticsType type) {
		return switch (type) {
			case NEW_USERS -> userService.getTotalUserCount();
			case DELETED_USERS -> userService.getTotalDeletedUsersCount();
			case NEW_ROUTINES -> routineService.getTotalRoutineCount();
			case NEW_DIARIES -> diaryService.getTotalDiaryCount();
			case NEW_SOCIAL_POSTS -> socialBoardService.getTotalSocialPostsCount();
			case NEW_COMMENTS -> socialBoardService.getTotalCommentsCount();
			case NEW_SCHEDULES -> scheduleReadService.getTotalSchedulesCount();
		};
	}

	private long getStatisticsCountByTypeAndDateRange(
		final StatisticsType type,
		final LocalDate startDate,
		final LocalDate endDate
	) {
		return switch (type) {
			case NEW_USERS -> userService.getNewUsersCountByDateRange(startDate, endDate);
			case DELETED_USERS -> userService.getDeletedUsersCountByDateRange(startDate, endDate);
			case NEW_ROUTINES -> routineService.getRoutineCountByDateRange(startDate, endDate);
			case NEW_DIARIES -> diaryService.getDiaryCountByDateRange(startDate, endDate);
			case NEW_SOCIAL_POSTS -> socialBoardService.getSocialPostsCountByDateRange(startDate, endDate);
			case NEW_COMMENTS -> socialBoardService.getCommentsCountByDateRange(startDate, endDate);
			case NEW_SCHEDULES -> scheduleReadService.getSchedulesCountByDateRange(startDate, endDate);
		};
	}

}
