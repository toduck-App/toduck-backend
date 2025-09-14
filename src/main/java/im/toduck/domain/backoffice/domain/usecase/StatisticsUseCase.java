package im.toduck.domain.backoffice.domain.usecase;

import java.time.LocalDate;

import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.backoffice.common.mapper.StatisticsMapper;
import im.toduck.domain.backoffice.presentation.dto.response.DailyStatisticsResponse;
import im.toduck.domain.backoffice.presentation.dto.response.OverallStatisticsResponse;
import im.toduck.domain.backoffice.presentation.dto.response.PeriodStatisticsResponse;
import im.toduck.domain.diary.domain.service.DiaryService;
import im.toduck.domain.routine.domain.service.RoutineService;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.global.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class StatisticsUseCase {

	private final UserService userService;
	private final DiaryService diaryService;
	private final RoutineService routineService;

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
	public DailyStatisticsResponse getDailyStatistics(final LocalDate date) {
		long newUsersCount = userService.getNewUsersCountByDateRange(date, date);
		long deletedUsersCount = userService.getDeletedUsersCountByDateRange(date, date);
		long newDiariesCount = diaryService.getDiaryCountByDateRange(date, date);
		long newRoutinesCount = routineService.getRoutineCountByDateRange(date, date);

		log.info("백오피스 일일 통계 조회 - 날짜: {}, 신규 회원: {}, 신규 일기: {}, 신규 루틴: {}",
			date, newUsersCount, newDiariesCount, newRoutinesCount);

		return StatisticsMapper.toDailyStatisticsResponse(
			newUsersCount, deletedUsersCount, newDiariesCount,
			newRoutinesCount, date
		);
	}
}
