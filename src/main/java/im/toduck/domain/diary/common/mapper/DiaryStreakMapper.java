package im.toduck.domain.diary.common.mapper;

import java.time.LocalDate;

import im.toduck.domain.diary.persistence.entity.DiaryStreak;
import im.toduck.domain.diary.presentation.dto.response.DiaryStreakResponse;
import im.toduck.domain.user.persistence.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DiaryStreakMapper {
	public static DiaryStreakResponse toDiaryStreakResponse(
		final DiaryStreak diaryStreak
	) {
		return DiaryStreakResponse.builder()
			.streak(diaryStreak.getStreak())
			.lastDiaryDate(diaryStreak.getLastDiaryDate())
			.build();
	}

	public static DiaryStreak toDiaryStreak(
		final User user,
		final Long streak,
		final LocalDate today
	) {
		return DiaryStreak.builder()
			.user(user)
			.streak(streak)
			.lastDiaryDate(today)
			.build();
	}
}
