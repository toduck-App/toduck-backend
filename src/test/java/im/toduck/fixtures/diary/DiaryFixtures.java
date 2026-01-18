package im.toduck.fixtures.diary;

import java.time.LocalDate;

import im.toduck.domain.diary.persistence.entity.Diary;
import im.toduck.domain.user.persistence.entity.Emotion;
import im.toduck.domain.user.persistence.entity.User;

public class DiaryFixtures {

	public static Diary DIARY(User user, LocalDate date) {
		return Diary.builder()
			.user(user)
			.date(date)
			.emotion(Emotion.HAPPY)
			.title("오늘의 일기")
			.memo("오늘은 정말 보람찬 하루였다.")
			.build();
	}
}
