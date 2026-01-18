package im.toduck.domain.badge.domain.checker;

import java.time.LocalDate;
import java.time.YearMonth;

import org.springframework.stereotype.Component;

import im.toduck.domain.badge.persistence.entity.BadgeCode;
import im.toduck.domain.diary.persistence.repository.DiaryRepository;
import im.toduck.domain.user.persistence.entity.User;
import lombok.RequiredArgsConstructor;

/**
 * 하루일기 뱃지 체커: 감정일기 한 달 작성률 50% 이상 달성 시 지급
 */
@Component
@RequiredArgsConstructor
public class DailyDiaryBadgeChecker implements BadgeConditionChecker {
	private static final int FIRST_DAY_OF_MONTH = 1;
	private static final double MINIMUM_WRITTEN_RATIO = 0.5;

	private final DiaryRepository diaryRepository;

	@Override
	public BadgeCode getBadgeCode() {
		return BadgeCode.DAILY_DIARY;
	}

	@Override
	public boolean checkCondition(final User user) {
		YearMonth currentMonth = YearMonth.now();
		LocalDate startDate = currentMonth.atDay(FIRST_DAY_OF_MONTH);
		LocalDate endDate = currentMonth.atEndOfMonth();
		int daysInMonth = currentMonth.lengthOfMonth();

		long writtenDays = diaryRepository.countDistinctDateByUserAndDateBetween(user, startDate, endDate);

		return writtenDays >= daysInMonth * MINIMUM_WRITTEN_RATIO;
	}
}
