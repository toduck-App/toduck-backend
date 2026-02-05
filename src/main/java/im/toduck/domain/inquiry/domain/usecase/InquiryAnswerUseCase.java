package im.toduck.domain.inquiry.domain.usecase;

import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.admin.domain.service.AdminService;
import im.toduck.domain.admin.persistence.entity.Admin;
import im.toduck.domain.inquiry.domain.service.InquiryAnswerService;
import im.toduck.domain.inquiry.persistence.entity.InquiryAnswer;
import im.toduck.domain.inquiry.presentation.dto.request.InquiryAnswerCreateRequest;
import im.toduck.domain.inquiry.presentation.dto.request.InquiryAnswerUpdateRequest;
import im.toduck.domain.user.domain.service.UserService;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.annotation.UseCase;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class InquiryAnswerUseCase {
	private final UserService userService;
	private final AdminService adminService;
	private final InquiryAnswerService inquiryAnswerService;

	@Transactional
	public InquiryAnswer createInquiryAnswer(final InquiryAnswerCreateRequest request, final Long userId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		Admin admin = adminService.getAdminBySameUser(user.getId());

		return inquiryAnswerService.createInquiryAnswer(request, admin);
	}

	@Transactional
	public InquiryAnswer updateInquiryAnswer(
		final Long inquiryId,
		final InquiryAnswerUpdateRequest request,
		final Long userId) {
		User user = userService.getUserById(userId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_USER));

		Admin admin = adminService.getAdminBySameUser(userId);

		return inquiryAnswerService.updateInquiryAnswer(inquiryId, request, admin);
	}

	@Transactional
	public void deleteInquiryAnswer(final Long inquiryId) {
		inquiryAnswerService.deleteInquiryAnswer(inquiryId);
	}
}
