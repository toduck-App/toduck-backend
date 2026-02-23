package im.toduck.domain.inquiry.common.mapper;

import im.toduck.domain.inquiry.persistence.entity.Inquiry;
import im.toduck.domain.inquiry.persistence.entity.InquiryImage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InquiryImgMapper {
	public static InquiryImage toInquiryImg(Inquiry inquiry, String imgUrls) {
		return InquiryImage.builder()
			.inquiry(inquiry)
			.url(imgUrls)
			.build();
	}

	public static String fromInquiryImg(InquiryImage inquiryImage) {
		return inquiryImage.getUrl();
	}
}
