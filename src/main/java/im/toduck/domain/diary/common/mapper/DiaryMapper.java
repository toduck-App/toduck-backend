package im.toduck.domain.diary.common.mapper;

import org.springframework.validation.annotation.Validated;

import im.toduck.domain.diary.persistence.entity.Diary;
import im.toduck.domain.diary.presentation.dto.request.DiaryCreateRequest;
import im.toduck.domain.diary.presentation.dto.response.DiaryCreateResponse;
import im.toduck.domain.user.persistence.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DiaryMapper {
	public static Diary toDiary(
		final User user,
		@Validated final DiaryCreateRequest request
	) {
		return Diary.builder()
			.user(user)
			.date(request.date())
			.emotion(request.emotion())
			.title(request.title())
			.memo(request.memo())
			.build();
	}

	public static DiaryCreateResponse toDiaryCreateResponse(Diary diary) {
		return DiaryCreateResponse.builder()
			.diaryId(diary.getId())
			.build();
	}
}
