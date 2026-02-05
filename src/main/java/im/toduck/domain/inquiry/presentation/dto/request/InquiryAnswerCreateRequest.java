package im.toduck.domain.inquiry.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "문의 답변 생성 요청 DTO")
public record InquiryAnswerCreateRequest(
	@NotNull(message = "문의 ID는 비어있을 수 없습니다.")
	@Schema(description = "문의 ID", example = "1")
	Long inquiryId,

	@NotNull
	@Size(max = 1023, message = "문의 답변은 1023자를 초과할 수 없습니다.")
	@Schema(description = "문의 답변", example = "현재로써는 루틴을 반복할 기간을 따로 설정할 수 있는 기능은 없습니다!")
	String content
) {
}
