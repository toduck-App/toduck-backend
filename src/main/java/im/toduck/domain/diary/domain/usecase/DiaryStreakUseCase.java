package im.toduck.domain.diary.domain.usecase;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.diary.domain.service.DiaryStreakService;
import im.toduck.domain.diary.persistence.entity.DiaryStreak;
import im.toduck.domain.diary.presentation.dto.response.DiaryStreakResponse;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.annotation.UseCase;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class DiaryStreakUseCase {
	private final UserService userService;
	private final DiaryStreakService diaryStreakService;

	@Transactional
	public DiaryStreakResponse getDiaryStreak(final Long userId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		DiaryStreakResponse response = diaryStreakService.getDiaryStreakAndLastDiaryDate(user.getId());
		LocalDate today = LocalDate.now();
		LocalDate lastDiaryDate = response.lastDiaryDate();
		Long streak = response.streak();

		if (lastDiaryDate != null
			&& ChronoUnit.DAYS.between(lastDiaryDate, today) > 1
			&& streak > 0) {
			Optional<DiaryStreak> optionalDiaryStreak = diaryStreakService.getDiaryStreak(userId);
			if (optionalDiaryStreak.isEmpty()) {
				return DiaryStreakResponse.empty();
			}
			DiaryStreak diaryStreak = optionalDiaryStreak.get();
			diaryStreakService.updateDiaryStreak(diaryStreak, 0L, diaryStreak.getLastDiaryDate());
			response = diaryStreakService.getDiaryStreakAndLastDiaryDate(user.getId());
		}
		return response;
	}

	@Transactional
	public void updateStreak(final Long userId, final LocalDate requestDate, final LocalDate today) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		Optional<DiaryStreak> optionalDiaryStreak = diaryStreakService.getDiaryStreak(user.getId());

		if (optionalDiaryStreak.isEmpty()) {
			if (requestDate == today) {
				diaryStreakService.createDiaryStreak(user, 1L, today);
			}
			return;
		}

		DiaryStreak diaryStreak = optionalDiaryStreak.get();

		Long streak = diaryStreak.getStreak();
		LocalDate lastDiaryDate = diaryStreak.getLastDiaryDate();

		if (!today.isEqual(requestDate)) {
			return;
		}

		if (requestDate.isEqual(lastDiaryDate)) {
			return;
		}

		if (requestDate.isEqual(lastDiaryDate.plusDays(1))) {
			diaryStreakService.updateDiaryStreak(diaryStreak, streak + 1, today);
		} else {
			diaryStreakService.updateDiaryStreak(diaryStreak, 1L, today);
		}
	}
}
