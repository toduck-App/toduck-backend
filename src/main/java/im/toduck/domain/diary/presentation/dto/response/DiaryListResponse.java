package im.toduck.domain.diary.presentation.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "월별 일기 목록 응답")
public record DiaryListResponse(
	@Schema(description = "일기 목록")
	List<DiaryResponse> diaryDtos
) {
	public static DiaryListResponse from(List<DiaryResponse> diaries) {
		return new DiaryListResponse(diaries);
	}
}

