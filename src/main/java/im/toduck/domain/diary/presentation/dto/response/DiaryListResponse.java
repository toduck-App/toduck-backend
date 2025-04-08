package im.toduck.domain.diary.presentation.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "월별 일기 목록 응답")
@Builder
public record DiaryListResponse(
	@Schema(description = "일기 목록")
	List<DiaryResponse> diaryDtos
) {
	public static DiaryListResponse toListDiaryResponse(List<DiaryResponse> diaries) {
		return DiaryListResponse.builder()
			.diaryDtos(diaries)
			.build();
	}
}

