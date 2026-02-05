package im.toduck.domain.inquiry.presentation.dto.request;

import java.util.List;

import im.toduck.domain.inquiry.persistence.entity.Type;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "문의 수정 요청 DTO")
public record InquiryUpdateRequest(
	@Schema(description = "문의 ID", example = "1")
	Long inquiryId,

	@Schema(description = "문의 유형", example = "USAGE")
	Type type,

	@Schema(description = "문의 내용", example = "루틴을 매 달 반복되도록 설정할 수 있나요?")
	String content,

	@Schema(description = "변경된 이미지 URL 목록", example = "[\"https://cdn.app/image123.jpg\"]")
	List<String> inquiryImgs
) {
}
