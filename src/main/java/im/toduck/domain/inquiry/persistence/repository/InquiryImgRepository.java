package im.toduck.domain.inquiry.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import im.toduck.domain.inquiry.persistence.entity.Inquiry;
import im.toduck.domain.inquiry.persistence.entity.InquiryImage;

@Repository
public interface InquiryImgRepository extends JpaRepository<InquiryImage, Long> {
	List<InquiryImage> findAllByInquiry(Inquiry inquiry);
}
