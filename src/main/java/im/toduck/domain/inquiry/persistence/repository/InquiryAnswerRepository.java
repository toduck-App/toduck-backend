package im.toduck.domain.inquiry.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import im.toduck.domain.inquiry.persistence.entity.Inquiry;
import im.toduck.domain.inquiry.persistence.entity.InquiryAnswer;

@Repository
public interface InquiryAnswerRepository extends JpaRepository<InquiryAnswer, Long> {
	boolean existsByInquiry(Inquiry inquiry);

	@Query(value = """
		SELECT *
		FROM inquiry_answer
		WHERE inquiry_id = :inquiryId
		""", nativeQuery = true)
	Optional<InquiryAnswer> findAnyByInquiryIdIncludingDeleted(Long inquiryId);
}
