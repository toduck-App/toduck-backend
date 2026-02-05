package im.toduck.domain.inquiry.domain.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.admin.persistence.entity.Admin;
import im.toduck.domain.inquiry.persistence.entity.Inquiry;
import im.toduck.domain.inquiry.persistence.entity.InquiryAnswer;
import im.toduck.domain.inquiry.persistence.entity.Status;
import im.toduck.domain.inquiry.persistence.repository.InquiryAnswerRepository;
import im.toduck.domain.inquiry.persistence.repository.InquiryRepository;
import im.toduck.domain.inquiry.presentation.dto.request.InquiryAnswerCreateRequest;
import im.toduck.domain.inquiry.presentation.dto.request.InquiryAnswerUpdateRequest;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InquiryAnswerService {
	private final InquiryRepository inquiryRepository;
	private final InquiryAnswerRepository inquiryAnswerRepository;

	@Transactional
	public Optional<InquiryAnswer> findById(final Long inquiryAnswerId) {
		return inquiryAnswerRepository.findById(inquiryAnswerId);
	}

	@Transactional
	public InquiryAnswer createInquiryAnswer(final InquiryAnswerCreateRequest request, final Admin admin) {
		Inquiry inquiry = inquiryRepository.findById(request.inquiryId())
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_INQUIRY));

		Optional<InquiryAnswer> anyAnswerOpt =
			inquiryAnswerRepository.findAnyByInquiryIdIncludingDeleted(inquiry.getId());

		if (anyAnswerOpt.isPresent()) {
			InquiryAnswer existing = anyAnswerOpt.get();

			if (existing.getDeletedAt() == null) {
				throw CommonException.from(ExceptionCode.ALREADY_ANSWERED_INQUIRY);
			}

			existing.revive(request.content(), admin);
			inquiry.addAnswer(existing);
			inquiry.changeStatus(Status.ANSWERED);
			return inquiryAnswerRepository.save(existing);
		}

		InquiryAnswer newAnswer = InquiryAnswer.builder()
			.admin(admin)
			.inquiry(inquiry)
			.content(request.content())
			.build();

		inquiry.addAnswer(newAnswer);
		inquiryAnswerRepository.save(newAnswer);
		inquiry.changeStatus(Status.ANSWERED);

		return newAnswer;
	}

	@Transactional
	public InquiryAnswer updateInquiryAnswer(
		final Long inquiryId,
		final InquiryAnswerUpdateRequest request,
		final Admin admin) {
		Inquiry inquiry = inquiryRepository.findById(inquiryId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_INQUIRY));

		InquiryAnswer inquiryAnswer = inquiry.getInquiryAnswer();
		inquiryAnswer.updateAnswer(request.content(), admin);
		return inquiryAnswer;
	}

	@Transactional
	public void deleteInquiryAnswer(final Long inquiryId) {
		Inquiry inquiry = inquiryRepository.findById(inquiryId)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_INQUIRY));

		InquiryAnswer inquiryAnswer = inquiry.getInquiryAnswer();
		if (inquiryAnswer == null) {
			throw CommonException.from(ExceptionCode.NOT_FOUND_INQUIRY_ANSWER);
		}

		inquiry.changeStatus(Status.PENDING);
		inquiry.removeAnswer();

		inquiryAnswerRepository.delete(inquiryAnswer);
	}
}
