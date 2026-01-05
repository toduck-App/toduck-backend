package im.toduck.domain.social.persistence.repository.querydsl;

import java.time.LocalDateTime;
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
import im.toduck.global.persistence.helper.DailyCountQueryHelper;
import im.toduck.global.persistence.projection.DailyCount;
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
			.leftJoin(qSocial.user).fetchJoin()
			.leftJoin(qSocial.routine).fetchJoin()
			.where(
				qSocial.deletedAt.isNull(),
				excludeBlockedUsers(currentUserId),
				cursorCondition(cursor)
			);

		applyCategoryFilter(query, categoryIds);

		return applyPagination(query, pageable).fetch();
	}

	@Override
	public List<Social> searchSocialsExcludingBlocked(
		Long cursor,
		Long currentUserId,
		String keyword,
		List<Long> categoryIds,
		Pageable pageable
	) {
		JPAQuery<Social> query = queryFactory
			.selectFrom(qSocial)
			.leftJoin(qSocial.user).fetchJoin()
			.leftJoin(qSocial.routine).fetchJoin()
			.where(
				qSocial.deletedAt.isNull(),
				excludeBlockedUsers(currentUserId),
				cursorCondition(cursor),
				keywordCondition(keyword)
			);

		applyCategoryFilter(query, categoryIds);

		return applyPagination(query, pageable).fetch();
	}

	@Override
	public List<Social> findUserSocials(
		Long profileUserId,
		Long cursor,
		Pageable pageable
	) {
		JPAQuery<Social> query = queryFactory
			.selectFrom(qSocial)
			.leftJoin(qSocial.user).fetchJoin()
			.where(
				qSocial.deletedAt.isNull(),
				qSocial.user.id.eq(profileUserId),
				cursorCondition(cursor)
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

	private void applyCategoryFilter(JPAQuery<Social> query, List<Long> categoryIds) {
		if (categoryIds != null && !categoryIds.isEmpty()) {
			query
				.join(qSocialCategoryLink).on(qSocialCategoryLink.social.eq(qSocial))
				.where(qSocialCategoryLink.socialCategory.id.in(categoryIds))
				.groupBy(qSocial.id)
				.having(qSocialCategoryLink.socialCategory.id.countDistinct().eq((long)categoryIds.size()));
		}
	}

	@Override
	public List<DailyCount> countByCreatedAtBetweenGroupByDate(
		final LocalDateTime startDateTime,
		final LocalDateTime endDateTime
	) {
		return DailyCountQueryHelper.countGroupByDate(
			queryFactory, qSocial, qSocial.createdAt, qSocial.count(), startDateTime, endDateTime
		);
	}
}
