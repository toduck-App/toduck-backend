package im.toduck.domain.diary.domain.usecase;

import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.diary.domain.service.DiaryStreakService;
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

	@Transactional(readOnly = true)
	public DiaryStreakResponse getDiaryStreak(final Long userId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		return diaryStreakService.getDiaryStreakAndLastDiaryDate(user.getId());
	}
}
