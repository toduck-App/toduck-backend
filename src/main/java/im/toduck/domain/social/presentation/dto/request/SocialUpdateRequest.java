package im.toduck.domain.social.presentation.dto.request;

import java.util.List;

import im.toduck.global.annotation.valid.ValidCategoryIds;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SocialUpdateRequest(
	@NotNull
	@Schema(description = "제목 변경 여부", example = "true")
	boolean isChangeTitle,

	@Nullable
	@Size(min = 1, max = 100, message = "제목은 공백일 수 없으며 100자 이하여야 합니다.")
	@Schema(description = "게시글 수정 제목", example = "오늘의 루틴 업데이트!")
	String title,

	@NotNull
	@Schema(description = "공유할 루틴 변경 여부", example = "true")
	boolean isChangeRoutine,

	@Nullable
	@Schema(description = "공유할 루틴 ID", example = "1")
	Long routineId,

	@Nullable
	@Size(min = 1, max = 255, message = "내용은 공백일 수 없으며 255자 이하여야 합니다.")
	@Schema(description = "게시글 수정 내용", example = "덕분에 오늘은 까먹을 일 없이 챙김!")
	String content,

	@Nullable
	@Schema(description = "익명 여부 수정", example = "true")
	Boolean isAnonymous,

	@ValidCategoryIds
	@Schema(description = "수정된 카테고리 ID 목록", example = "[3, 4]")
	List<Long> socialCategoryIds,

	@Nullable
	@Size(max = 5, message = "이미지는 최대 5개까지만 등록할 수 있습니다.")
	@Schema(description = "수정된 이미지 URL 목록", example = "[\"https://cdn.toduck.app/image2.jpg\"]")
	List<String> socialImageUrls
) {
	@AssertTrue(message = "루틴 변경이 없을 때는 routineId가 null이어야 합니다.")
	public boolean isValidRoutineIdWhenRemoved() {
		return isChangeRoutine || routineId == null;
	}

	@AssertTrue(message = "제목 변경이 없을 때는 title이 null이어야 합니다.")
	public boolean isValidTitleWhenRemoved() {
		return isChangeTitle || title == null;
	}
}
