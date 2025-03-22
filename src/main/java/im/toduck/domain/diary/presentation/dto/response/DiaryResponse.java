package im.toduck.domain.diary.presentation.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import im.toduck.domain.diary.persistence.entity.Diary;
import im.toduck.domain.user.persistence.entity.Emotion;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record DiaryResponse(
	@Schema(description = "날짜", example = "2025-03-21")
	LocalDate date,

	@Schema(description = "감정", example = "HAPPY")
	Emotion emotion,

	@Schema(description = "제목", example = "행복한 기분")
	String title,

	@Schema(description = "메모", example = "오늘은 좋은 일이 있었다")
	String memo,

	@Schema(description = "일기 이미지 목록")
	List<DiaryImageDto> diaryImages
) {
	public static DiaryResponse fromEntity(Diary diary) {
		return new DiaryResponse(
			diary.getDate(),
			diary.getEmotion(),
			diary.getTitle(),
			diary.getMemo(),
			diary.getDiaryImages().stream()
				.map(DiaryImageDto::fromEntity)
				.collect(Collectors.toList())
		);
	}
}
