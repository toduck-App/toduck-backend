package im.toduck.domain.inquiry.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "문의 답변 수정 요청 DTO")
public record InquiryAnswerUpdateRequest(
	@NotNull
	@Size(max = 1023, message = "문의 답변은 1023자를 초과할 수 없습니다.")
	@Schema(description = "문의 답변", example = "도움이 되셨기를 바라며, 좋은 하루 되세요 :)")
	String content
) {
}
