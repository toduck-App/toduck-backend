package im.toduck.domain.inquiry.persistence.repository.querydsl;

import java.util.List;

import im.toduck.domain.inquiry.persistence.entity.Inquiry;

public interface InquiryRepositoryCustom {
	List<Inquiry> findWithImgs(final Long userId);

	List<Inquiry> findAllWithImgs();
}
