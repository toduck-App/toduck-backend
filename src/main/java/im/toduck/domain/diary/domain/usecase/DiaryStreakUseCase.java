package im.toduck.domain.diary.domain.usecase;

import java.time.LocalDate;
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

		return diaryStreakService.synchronizeAndGetFromDb(user.getId());
	}

	public DiaryStreakResponse getCachedDiaryStreak(final Long userId) {
		DiaryStreakResponse response = diaryStreakService.getCachedDiaryStreakAndLastDiaryDate(userId);
		return response;
	}

	@Transactional
	public void updateStreak(final Long userId, final LocalDate requestDate, final LocalDate today) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		// 일기 작성 시 해당 함수 실행
		Optional<DiaryStreak> optionalDiaryStreak = diaryStreakService.getDiaryStreak(user.getId());

		if (optionalDiaryStreak.isEmpty()) { // 스트릭 최초 생성시
			if (today.isEqual(requestDate)) { // 오늘 날짜에 일기를 작성하는 경우
				diaryStreakService.createDiaryStreak(user, 1L, today);
			}
			return;
		}

		DiaryStreak diaryStreak = optionalDiaryStreak.get();

		Long streak = diaryStreak.getStreak();
		LocalDate lastDiaryDate = diaryStreak.getLastDiaryDate();

		if (!today.isEqual(requestDate)) { // 오늘 날짜랑 일기 작성 날짜가 다른 경우 예외처리
			return;
		}

		if (lastDiaryDate == null) {
			diaryStreakService.updateDiaryStreak(diaryStreak, 1L, today);
			return;
		}

		if (requestDate.isEqual(lastDiaryDate)) { // 이미 오늘 일기를 작성한 경우 예외처리
			return;
		}

		if (requestDate.isEqual(lastDiaryDate.plusDays(1))) { // 최근 스트릭이 저장된 날이랑 비교해서 하루 차이나면
			diaryStreakService.updateDiaryStreak(diaryStreak, streak + 1, today); // 기존 스트릭 + 1
		} else {
			diaryStreakService.updateDiaryStreak(diaryStreak, 1L, today); // 이틀 이상 차이나면 1로 초기화
		}
	}
}
