package im.toduck.domain.inquiry.domain.usecase;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.inquiry.common.mapper.InquiryMapper;
import im.toduck.domain.inquiry.domain.service.InquiryService;
import im.toduck.domain.inquiry.persistence.entity.Inquiry;
import im.toduck.domain.inquiry.persistence.entity.InquiryAnswer;
import im.toduck.domain.inquiry.persistence.entity.InquiryImage;
import im.toduck.domain.inquiry.presentation.dto.request.InquiryCreateRequest;
import im.toduck.domain.inquiry.presentation.dto.request.InquiryUpdateRequest;
import im.toduck.domain.inquiry.presentation.dto.response.InquiryListResponse;
import im.toduck.domain.inquiry.presentation.dto.response.InquiryResponse;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.annotation.UseCase;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class InquiryUseCase {
	private final UserService userService;
	private final InquiryService inquiryService;

	@Transactional(readOnly = true)
	public InquiryListResponse getInquiries(Long userId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		List<InquiryResponse> inquiries = inquiryService.getInquiries(userId);

		return InquiryMapper.toListInquiryResponse(inquiries);
	}

	@Transactional
	public Inquiry createInquiry(final InquiryCreateRequest request, final Long userId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		Inquiry inquiry = inquiryService.createInquiry(request, user);
		inquiryService.addInquiryImages(inquiry, request.inquiryImgs());

		return inquiry;
	}

	@Transactional
	public Inquiry updateInquiry(
		final Long inquiryId,
		final InquiryUpdateRequest request,
		final Long userId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		Inquiry inquiry = inquiryService.getInquiryById(inquiryId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_INQUIRY));

		InquiryAnswer answer = inquiry.getInquiryAnswer();
		if (answer != null && answer.getDeletedAt() == null) {
			throw CommonException.from(ExceptionCode.ALREADY_ANSWERED_INQUIRY);
		}

		inquiryService.updateInquiry(request, inquiry);
		return inquiry;
	}

	@Transactional
	public void deleteInquiry(final Long inquiryId, final Long userId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		Inquiry inquiry = inquiryService.findById(inquiryId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_INQUIRY));

		InquiryAnswer answer = inquiry.getInquiryAnswer();
		if (answer != null && answer.getDeletedAt() == null) {
			throw CommonException.from(ExceptionCode.ALREADY_ANSWERED_INQUIRY);
		}

		inquiry.getInquiryImages().forEach(InquiryImage::softDelete);

		inquiryService.deleteInquiry(inquiry);
	}

	@Transactional(readOnly = true)
	public InquiryListResponse getAllInquiries(final Long userId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		List<InquiryResponse> inquiries = inquiryService.getAllInquiries();

		return InquiryMapper.toListInquiryResponse(inquiries);
	}
}
