package im.toduck.domain.diary.presentation.dto.request;

import java.util.List;

import im.toduck.domain.user.persistence.entity.Emotion;
import io.micrometer.common.lang.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DiaryUpdateRequest(
	@NotNull
	@Schema(description = "감정 변경 여부", example = "true")
	boolean isChangeEmotion,

	@NotNull
	@Schema(description = "기존/변경된 감정", example = "HAPPY")
	Emotion emotion,

	@Nullable
	@Size(max = 50, message = "제목은 공백일 수 없으며 50자 이하여야 합니다.")
	@Schema(description = "변경된 제목", example = "오늘의 기분은 최고!")
	String title,

	@Nullable
	@Size(max = 2048, message = "메모는 공백일 수 없으며 2048자 이하여야 합니다.")
	@Schema(description = "변경된 메모", example = "오늘 하루를 기록해봅니다...")
	String memo,

	@Nullable
	@Size(max = 2, message = "이미지는 최대 2개까지만 등록할 수 있습니다.")
	@Schema(description = "변경된 이미지 URL 목록", example = "[\"https://cdn.app/image1.jpg\"]")
	List<String> diaryImageUrls
) {
}
