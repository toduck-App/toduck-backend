package im.toduck.domain.diary.common.mapper;

import im.toduck.domain.diary.persistence.entity.DiaryStreak;
import im.toduck.domain.diary.presentation.dto.response.DiaryStreakResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DiaryStreakMapper {
	public static DiaryStreakResponse toDiaryStreakResponse(
		final DiaryStreak diaryStreak
	) {
		if (diaryStreak == null) {
			return DiaryStreakResponse.builder()
				.consecutiveDays(0L)
				.lastDiaryDate(null)
				.build();
		}

		return DiaryStreakResponse.builder()
			.consecutiveDays(diaryStreak.getStreak())
			.lastDiaryDate(diaryStreak.getLastDiaryDate())
			.build();
	}
}
