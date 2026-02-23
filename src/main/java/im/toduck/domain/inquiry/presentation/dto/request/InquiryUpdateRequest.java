package im.toduck.domain.inquiry.presentation.dto.request;

import java.util.List;

import im.toduck.domain.inquiry.persistence.entity.Type;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "문의 수정 요청 DTO")
public record InquiryUpdateRequest(
	@NotNull(message = "문의 유형은 비어있을 수 없습니다.")
	@Schema(description = "문의 유형", example = "USAGE")
	Type type,

	@Size(max = 500, message = "내용은 500자를 초과할 수 없습니다.")
	@NotBlank(message = "내용은 비어있을 수 없습니다.")
	@Schema(description = "문의 내용", example = "루틴을 매 달 반복되도록 설정할 수 있나요?")
	String content,

	@Schema(description = "변경된 이미지 URL 목록", example = "[\"https://cdn.app/image123.jpg\"]")
	List<String> inquiryImgs
) {
}
