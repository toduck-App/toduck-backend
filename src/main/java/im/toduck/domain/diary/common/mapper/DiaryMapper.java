package im.toduck.domain.diary.common.mapper;

import java.util.List;

import im.toduck.domain.diary.persistence.entity.Diary;
import im.toduck.domain.diary.presentation.dto.request.DiaryCreateRequest;
import im.toduck.domain.diary.presentation.dto.response.DiaryKeywordDto;
import im.toduck.domain.diary.presentation.dto.response.DiaryListResponse;
import im.toduck.domain.diary.presentation.dto.response.DiaryResponse;
import im.toduck.domain.diary.presentation.dto.response.MonthDiaryResponse;
import im.toduck.domain.user.persistence.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DiaryMapper {
	public static Diary toDiary(
		final User user,
		final DiaryCreateRequest request
	) {
		return Diary.builder()
			.user(user)
			.date(request.date())
			.emotion(request.emotion())
			.title(request.title())
			.memo(request.memo())
			.build();
	}

	public static DiaryResponse fromDiary(Diary diary) {
		return new DiaryResponse(
			diary.getId(),
			diary.getDate(),
			diary.getEmotion(),
			diary.getTitle(),
			diary.getMemo(),
			diary.getDiaryImages().stream()
				.map(DiaryImageFileMapper::fromDiaryImage)
				.toList(),
			diary.getDiaryKeywords().stream()
				.map(DiaryKeywordDto::from)
				.toList()
		);
	}

	public static DiaryListResponse toListDiaryResponse(List<DiaryResponse> diaries) {
		return DiaryListResponse.toListDiaryResponse(diaries);
	}

	public static MonthDiaryResponse toMonthDiaryResponse(int thisMonthCount, int lastMonthCount) {
		return new MonthDiaryResponse(thisMonthCount - lastMonthCount);
	}
}
