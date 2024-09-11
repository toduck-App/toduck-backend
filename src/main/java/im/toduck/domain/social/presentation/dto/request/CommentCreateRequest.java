package im.toduck.domain.social.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CommentCreateRequest(
	@NotBlank(message = "내용을 입력해주세요.")
	@Schema(description = "댓글 내용", example = "루틴 너무 좋네요!")
	String content
) {
}
