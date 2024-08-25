package im.toduck.domain.social.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateCommentRequest(
	@NotBlank(message = "내용을 입력해주세요.")
	String content
) {
}
