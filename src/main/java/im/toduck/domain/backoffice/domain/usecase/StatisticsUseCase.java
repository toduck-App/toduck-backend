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
	public OverallStatisticsResponse getOverallStatistics() {
		long totalUserCount = userService.getTotalUserCount();
		long totalDiaryCount = diaryService.getTotalDiaryCount();
		long totalRoutineCount = routineService.getTotalRoutineCount();
		long activeDiaryWritersCount = diaryService.getActiveDiaryWritersCount();
		long activeRoutineUsersCount = routineService.getActiveRoutineUsersCount();

		log.info("백오피스 전체 통계 조회 - 총 회원수: {}, 총 일기수: {}, 총 루틴수: {}",
			totalUserCount, totalDiaryCount, totalRoutineCount);

		return StatisticsMapper.toOverallStatisticsResponse(
			totalUserCount, totalDiaryCount, totalRoutineCount,
			activeDiaryWritersCount, activeRoutineUsersCount
		);
	}

	@Transactional(readOnly = true)
	public PeriodStatisticsResponse getPeriodStatistics(final LocalDate startDate, final LocalDate endDate) {
		long newUsersCount = userService.getNewUsersCountByDateRange(startDate, endDate);
		long deletedUsersCount = userService.getDeletedUsersCountByDateRange(startDate, endDate);
		long newDiariesCount = diaryService.getDiaryCountByDateRange(startDate, endDate);
		long newRoutinesCount = routineService.getRoutineCountByDateRange(startDate, endDate);

		log.info("백오피스 기간별 통계 조회 - 기간: {} ~ {}, 신규 회원: {}, 탈퇴 회원: {}",
			startDate, endDate, newUsersCount, deletedUsersCount);

		return StatisticsMapper.toPeriodStatisticsResponse(
			newUsersCount, deletedUsersCount, newDiariesCount,
			newRoutinesCount, startDate, endDate
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

}
