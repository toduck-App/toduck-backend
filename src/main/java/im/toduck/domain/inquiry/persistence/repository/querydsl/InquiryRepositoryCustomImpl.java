package im.toduck.domain.inquiry.persistence.repository.querydsl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import im.toduck.domain.inquiry.persistence.entity.Inquiry;
import im.toduck.domain.inquiry.persistence.entity.QInquiry;
import im.toduck.domain.inquiry.persistence.entity.QInquiryAnswer;
import im.toduck.domain.inquiry.persistence.entity.QInquiryImage;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class InquiryRepositoryCustomImpl implements InquiryRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<Inquiry> findWithImgs(final Long userId) {
		QInquiry iq = QInquiry.inquiry;
		QInquiryImage iqi = QInquiryImage.inquiryImage;
		QInquiryAnswer iqa = QInquiryAnswer.inquiryAnswer;

		return queryFactory
			.selectFrom(iq)
			.leftJoin(iq.inquiryImages, iqi).fetchJoin()
			.leftJoin(iq.inquiryAnswer, iqa).fetchJoin()
			.leftJoin(iqa.admin).fetchJoin()
			.where(
				iq.user.id.eq(userId),
				iq.deletedAt.isNull()
			)
			.orderBy(iq.createdAt.desc())
			.distinct()
			.fetch();
	}

	@Override
	public List<Inquiry> findAllWithImgs() {
		QInquiry iq = QInquiry.inquiry;
		QInquiryImage iqi = QInquiryImage.inquiryImage;
		QInquiryAnswer iqa = QInquiryAnswer.inquiryAnswer;

		return queryFactory
			.selectFrom(iq)
			.leftJoin(iq.inquiryImages, iqi).fetchJoin()
			.leftJoin(iq.inquiryAnswer, iqa).fetchJoin()
			.leftJoin(iqa.admin).fetchJoin()
			.where(iq.deletedAt.isNull())
			.orderBy(iq.createdAt.desc())
			.distinct()
			.fetch();
	}
}
