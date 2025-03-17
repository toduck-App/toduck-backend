package im.toduck.domain.diary.common.mapper;

import java.time.LocalDate;

import im.toduck.domain.diary.persistence.entity.Diary;
import im.toduck.domain.diary.presentation.dto.response.DiaryCreateResponse;
import im.toduck.domain.user.persistence.entity.Emotion;
import im.toduck.domain.user.persistence.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DiaryMapper {
	public static Diary toDiary(
		final User user,
		final LocalDate date,
		final Emotion emotion,
		final String title,
		final String memo
	) {
		return Diary.builder()
			.user(user)
			.date(date)
			.emotion(emotion)
			.title(title)
			.memo(memo)
			.build();
	}

	public static DiaryCreateResponse toDiaryCreateResponse(Diary diary) {
		return DiaryCreateResponse.builder()
			.diaryId(diary.getId())
			.build();
	}
}
