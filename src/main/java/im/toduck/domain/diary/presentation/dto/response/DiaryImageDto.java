package im.toduck.domain.diary.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record DiaryImageDto(
	@Schema(description = "이미지 Id", example = "1")
	Long diaryImageId,

	@Schema(description = "이미지 URL", example = "https://cdn.toduck.app/image1.jpg")
	String url
) {

}
