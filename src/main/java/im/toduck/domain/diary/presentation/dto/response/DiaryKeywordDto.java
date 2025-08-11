package im.toduck.domain.diary.presentation.dto.response;

import im.toduck.domain.diary.persistence.entity.DiaryKeyword;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record DiaryKeywordDto(
	@Schema(description = "사용자 키워드 ID", example = "3")
	Long keywordId,

	@Schema(description = "키워드 이름", example = "행복")
	String keywordName,

	@Schema(description = "핵심 키워드", example = "true")
	boolean checked
) {
	public static DiaryKeywordDto from(DiaryKeyword diaryKeyword) {
		return DiaryKeywordDto.builder()
			.keywordId(diaryKeyword.getUserKeyword().getId())
			.keywordName(diaryKeyword.getUserKeyword().getKeyword())
			.build();
	}
}
