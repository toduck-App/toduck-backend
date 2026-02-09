package im.toduck.domain.inquiry.common.mapper;

import java.util.List;

import im.toduck.domain.inquiry.persistence.entity.Inquiry;
import im.toduck.domain.inquiry.persistence.entity.InquiryAnswer;
import im.toduck.domain.inquiry.persistence.entity.Status;
import im.toduck.domain.inquiry.presentation.dto.request.InquiryCreateRequest;
import im.toduck.domain.inquiry.presentation.dto.response.InquiryListResponse;
import im.toduck.domain.inquiry.presentation.dto.response.InquiryResponse;
import im.toduck.domain.user.persistence.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InquiryMapper {

	public static InquiryResponse fromInquiry(final Inquiry inquiry) {

		InquiryAnswer answer = inquiry.getInquiryAnswer();

		return new InquiryResponse(
			inquiry.getId(),
			inquiry.getType(),
			inquiry.getContent(),
			inquiry.getStatus(),
			inquiry.getCreatedAt(),
			inquiry.getInquiryImages().stream()
				.map(InquiryImgMapper::fromInquiryImg)
				.toList(),

			answer != null ? answer.getId() : null,
			answer != null ? answer.getAdmin().getDisplayName() : null,
			answer != null ? answer.getContent() : null,
			answer != null ? answer.getCreatedAt() : null
		);
	}

	public static InquiryListResponse toListInquiryResponse(final List<InquiryResponse> inquiries) {
		return InquiryListResponse.toListInquiryResponse(inquiries);
	}

	public static Inquiry toInquiry(final InquiryCreateRequest request, final User user) {
		return Inquiry.builder()
			.user(user)
			.type(request.type())
			.content(request.content())
			.status(Status.PENDING)
			.build();
	}
}
