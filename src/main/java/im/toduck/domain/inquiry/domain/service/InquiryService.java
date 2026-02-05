package im.toduck.domain.inquiry.domain.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.inquiry.common.mapper.InquiryImgMapper;
import im.toduck.domain.inquiry.common.mapper.InquiryMapper;
import im.toduck.domain.inquiry.persistence.entity.Inquiry;
import im.toduck.domain.inquiry.persistence.entity.InquiryImage;
import im.toduck.domain.inquiry.persistence.repository.InquiryImgRepository;
import im.toduck.domain.inquiry.persistence.repository.InquiryRepository;
import im.toduck.domain.inquiry.persistence.repository.querydsl.InquiryRepositoryCustom;
import im.toduck.domain.inquiry.presentation.dto.request.InquiryCreateRequest;
import im.toduck.domain.inquiry.presentation.dto.request.InquiryUpdateRequest;
import im.toduck.domain.inquiry.presentation.dto.response.InquiryResponse;
import im.toduck.domain.user.persistence.entity.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InquiryService {
	private final InquiryRepository inquiryRepository;
	private final InquiryImgRepository inquiryImgRepository;
	private final InquiryRepositoryCustom inquiryRepositoryCustom;

	@Transactional(readOnly = true)
	public List<InquiryResponse> getInquiries(final Long userId) {
		List<Inquiry> inquiries = inquiryRepositoryCustom.findWithImgs(userId);
		return inquiries.stream()
			.map(InquiryMapper::fromInquiry)
			.toList();
	}

	@Transactional
	public Inquiry createInquiry(final InquiryCreateRequest request, final User user) {
		Inquiry inquiry = InquiryMapper.toInquiry(request, user);
		inquiryRepository.save(inquiry);
		return inquiry;
	}

	@Transactional
	public void addInquiryImages(final Inquiry inquiry, final List<String> imgUrls) {
		List<String> safeImgs = Optional.ofNullable(imgUrls).orElse(Collections.emptyList());

		List<InquiryImage> inquiryImgs = safeImgs.stream()
			.map(url -> InquiryImgMapper.toInquiryImg(inquiry, url))
			.toList();
		inquiryImgRepository.saveAll(inquiryImgs);
	}

	@Transactional
	public Optional<Inquiry> getInquiryById(final Long inquiryId) {
		return inquiryRepository.findById(inquiryId);
	}

	@Transactional
	public void updateInquiry(final InquiryUpdateRequest request, final Inquiry inquiry) {
		if (request.type() != null) {
			inquiry.updateType(request.type());
		}

		if (request.content() != null) {
			inquiry.updateContent(request.content());
		}

		if (request.inquiryImgs() != null && !request.inquiryImgs().isEmpty()) {
			inquiryImgRepository.deleteAllByInquiry(inquiry);
			addInquiryImages(inquiry, request.inquiryImgs());
		} else {
			inquiryImgRepository.deleteAllByInquiry(inquiry);
		}
	}

	@Transactional
	public Optional<Inquiry> findById(final Long inquiryId) {
		return inquiryRepository.findById(inquiryId);
	}

	@Transactional
	public void deleteInquiry(final Inquiry inquiry) {
		inquiryRepository.delete(inquiry);
	}

	@Transactional(readOnly = true)
	public List<InquiryResponse> getAllInquiries() {
		List<Inquiry> inquiries = inquiryRepositoryCustom.findAllWithImgs();
		return inquiries.stream()
			.map(InquiryMapper::fromInquiry)
			.toList();
	}
}
