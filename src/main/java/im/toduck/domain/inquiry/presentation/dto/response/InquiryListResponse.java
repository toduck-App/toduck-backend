package im.toduck.domain.inquiry.presentation.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "문의 내역 목록 응답")
public record InquiryListResponse(
	@Schema(description = "문의 내역 목록")
	List<InquiryResponse> inquiryDtos
) {
	public static InquiryListResponse toListInquiryResponse(
		final List<InquiryResponse> inquiries
	) {
		return new InquiryListResponse(inquiries);
	}
}
