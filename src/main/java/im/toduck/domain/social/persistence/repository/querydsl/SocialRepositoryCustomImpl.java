package im.toduck.domain.social.persistence.repository.querydsl;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import im.toduck.domain.social.persistence.entity.QSocial;
import im.toduck.domain.social.persistence.entity.QSocialCategory;
import im.toduck.domain.social.persistence.entity.QSocialCategoryLink;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.user.persistence.entity.QBlock;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SocialRepositoryCustomImpl implements SocialRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	private final QSocial qSocial = QSocial.social;
	private final QBlock qBlock = QBlock.block;
	private final QSocialCategoryLink qSocialCategoryLink = QSocialCategoryLink.socialCategoryLink;
	private final QSocialCategory qSocialCategory = QSocialCategory.socialCategory;

	@Override
	public List<Social> findSocialsExcludingBlocked(
		Long cursor,
		Long currentUserId,
		List<Long> categoryIds,
		Pageable pageable
	) {
		JPAQuery<Social> query = queryFactory
			.selectFrom(qSocial)
			.where(
				qSocial.deletedAt.isNull(),
				excludeBlockedUsers(currentUserId),
				cursorCondition(cursor)
			);

		if (categoryIds != null && !categoryIds.isEmpty()) {
			query.join(qSocialCategoryLink).on(qSocialCategoryLink.social.eq(qSocial))
				.join(qSocialCategory).on(qSocialCategoryLink.socialCategory.eq(qSocialCategory))
				.where(qSocialCategory.id.in(categoryIds));
			query.distinct();
		}

		return applyPagination(query, pageable).fetch();
	}

	@Override
	public List<Social> searchSocialsExcludingBlocked(
		Long cursor,
		Long currentUserId,
		String keyword,
		Pageable pageable
	) {
		JPAQuery<Social> query = queryFactory
			.selectFrom(qSocial)
			.where(
				qSocial.deletedAt.isNull(),
				excludeBlockedUsers(currentUserId),
				cursorCondition(cursor),
				keywordCondition(keyword)
			);

		return applyPagination(query, pageable).fetch();
	}

	private BooleanExpression keywordCondition(String keyword) {
		if (keyword == null || keyword.isEmpty()) {
			return null;
		}
		return qSocial.content.containsIgnoreCase(keyword)
			.or(qSocial.title.containsIgnoreCase(keyword));
	}

	private BooleanExpression excludeBlockedUsers(Long currentUserId) {
		if (currentUserId == null) {
			return null;
		}

		return qSocial.user.id.notIn(
			queryFactory
				.select(qBlock.blocked.id)
				.from(qBlock)
				.where(qBlock.blocker.id.eq(currentUserId))
		);
	}

	private BooleanExpression cursorCondition(Long cursor) {
		if (cursor == null) {
			return null;
		}
		return qSocial.id.lt(cursor);
	}

	private JPAQuery<Social> applyPagination(JPAQuery<Social> query, Pageable pageable) {
		return query
			.orderBy(qSocial.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize());
	}
}
