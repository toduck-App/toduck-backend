package im.toduck.domain.social.persistence.repository.querydsl;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import im.toduck.domain.social.persistence.entity.QSocial;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.user.persistence.entity.QBlock;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SocialRepositoryCustomImpl implements SocialRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	private final QSocial qSocial = QSocial.social;
	private final QBlock qBlock = QBlock.block;

	@Override
	public List<Social> findByIdBeforeOrderByIdDescExcludingBlocked(Long cursor, Long currentUserId,
		Pageable pageable) {
		return queryFactory
			.selectFrom(qSocial)
			.where(
				qSocial.id.lt(cursor),
				qSocial.user.id.notIn(
					queryFactory
						.select(qBlock.blocked.id)
						.from(qBlock)
						.where(qBlock.blocker.id.eq(currentUserId))
				)
			)
			.orderBy(qSocial.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	@Override
	public List<Social> findLatestSocialsExcludingBlocked(Long currentUserId, Pageable pageable) {
		return queryFactory
			.selectFrom(qSocial)
			.where(
				qSocial.deletedAt.isNull(),
				qSocial.user.id.notIn(
					queryFactory
						.select(qBlock.blocked.id)
						.from(qBlock)
						.where(qBlock.blocker.id.eq(currentUserId))
				)
			)
			.orderBy(qSocial.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}
}
