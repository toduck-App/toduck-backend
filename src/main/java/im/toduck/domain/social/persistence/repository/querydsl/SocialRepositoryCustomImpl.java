package im.toduck.domain.social.persistence.repository.querydsl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import im.toduck.domain.social.persistence.entity.QComment;
import im.toduck.domain.social.persistence.entity.QLike;
import im.toduck.domain.social.persistence.entity.QSocial;
import im.toduck.domain.social.persistence.entity.QSocialCategory;
import im.toduck.domain.social.persistence.entity.QSocialCategoryLink;
import im.toduck.domain.social.persistence.entity.QSocialImageFile;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.entity.SocialCategory;
import im.toduck.domain.social.persistence.entity.SocialImageFile;
import im.toduck.domain.user.persistence.entity.QBlock;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SocialRepositoryCustomImpl implements SocialRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QSocial social = QSocial.social;
	private final QBlock block = QBlock.block;
	private final QSocialCategoryLink socialCategoryLink = QSocialCategoryLink.socialCategoryLink;
	private final QSocialCategory socialCategory = QSocialCategory.socialCategory;
	private final QSocialImageFile socialImageFile = QSocialImageFile.socialImageFile;
	private final QComment comment = QComment.comment;
	private final QLike like = QLike.like;

	@Override
	public List<Social> findSocialsExcludingBlocked(
		Long cursor,
		Long currentUserId,
		List<Long> categoryIds,
		Pageable pageable
	) {
		JPAQuery<Social> query = queryFactory
			.selectFrom(social)
			.where(
				isNotDeleted()
					.and(excludeBlockedUsers(currentUserId))
					.and(cursorCondition(cursor))
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
			.selectFrom(social)
			.where(
				isNotDeleted()
					.and(excludeBlockedUsers(currentUserId))
					.and(cursorCondition(cursor))
					.and(keywordCondition(keyword))
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
		return queryFactory
			.selectFrom(social)
			.leftJoin(social.user).fetchJoin()
			.where(
				isNotDeleted()
					.and(social.user.id.eq(profileUserId))
					.and(cursorCondition(cursor))
			)
			.orderBy(social.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	@Override
	public Map<Long, List<SocialImageFile>> findImageFilesBySocialIds(List<Long> socialIds) {
		List<SocialImageFile> imageFiles = queryFactory
			.selectFrom(socialImageFile)
			.where(
				socialImageFile.social.id.in(socialIds)
					.and(socialImageFile.deletedAt.isNull())
			)
			.fetch();

		return imageFiles.stream()
			.collect(Collectors.groupingBy(img -> img.getSocial().getId()));
	}

	@Override
	public Map<Long, Integer> countCommentsBySocialIds(List<Long> socialIds) {
		List<Tuple> counts = queryFactory
			.select(comment.social.id, comment.count().intValue())
			.from(comment)
			.where(
				comment.social.id.in(socialIds)
					.and(comment.deletedAt.isNull())
			)
			.groupBy(comment.social.id)
			.fetch();

		return counts.stream()
			.filter(tuple -> tuple.get(comment.social.id) != null
				&& tuple.get(1, Integer.class) != null)
			.collect(Collectors.toMap(
				tuple -> Objects.requireNonNull(tuple.get(comment.social.id)),
				tuple -> Objects.requireNonNull(tuple.get(1, Integer.class))
			));
	}

	@Override
	public Map<Long, Boolean> findLikesBySocialIdsAndUserId(List<Long> socialIds, Long userId) {
		if (userId == null) {
			return socialIds.stream()
				.collect(Collectors.toMap(id -> id, id -> false));
		}

		List<Long> likedSocialIds = queryFactory
			.select(like.social.id)
			.from(like)
			.where(
				like.social.id.in(socialIds)
					.and(like.user.id.eq(userId))
					.and(like.deletedAt.isNull())
			)
			.fetch();

		Set<Long> likedSet = new HashSet<>(likedSocialIds);
		return socialIds.stream()
			.collect(Collectors.toMap(id -> id, likedSet::contains));
	}

	@Override
	public Map<Long, List<SocialCategory>> findCategoriesBySocialIds(List<Long> socialIds) {
		List<Tuple> categoryData = queryFactory
			.select(socialCategoryLink.social.id, socialCategory)
			.from(socialCategoryLink)
			.join(socialCategoryLink.socialCategory, socialCategory)
			.where(
				socialCategoryLink.social.id.in(socialIds)
					.and(socialCategoryLink.deletedAt.isNull())
			)
			.fetch();

		return categoryData.stream()
			.filter(tuple -> tuple.get(socialCategoryLink.social.id) != null
				&& tuple.get(socialCategory) != null)
			.collect(Collectors.groupingBy(
				tuple -> Objects.requireNonNull(tuple.get(socialCategoryLink.social.id)),
				Collectors.mapping(
					tuple -> Objects.requireNonNull(tuple.get(socialCategory)),
					Collectors.toList()
				)
			));
	}

	private BooleanExpression isNotDeleted() {
		return social.deletedAt.isNull();
	}

	private BooleanExpression excludeBlockedUsers(Long currentUserId) {
		if (currentUserId == null) {
			return null;
		}

		return social.user.id.notIn(
			queryFactory
				.select(block.blocked.id)
				.from(block)
				.where(block.blocker.id.eq(currentUserId))
		);
	}

	private BooleanExpression cursorCondition(Long cursor) {
		if (cursor == null) {
			return null;
		}
		return social.id.lt(cursor);
	}

	private BooleanExpression keywordCondition(String keyword) {
		if (keyword == null || keyword.isEmpty()) {
			return null;
		}
		return social.content.containsIgnoreCase(keyword)
			.or(social.title.containsIgnoreCase(keyword));
	}

	private void applyCategoryFilter(JPAQuery<Social> query, List<Long> categoryIds) {
		if (categoryIds != null && !categoryIds.isEmpty()) {
			query
				.join(socialCategoryLink).on(socialCategoryLink.social.eq(social))
				.where(socialCategoryLink.socialCategory.id.in(categoryIds))
				.groupBy(social.id)
				.having(socialCategoryLink.socialCategory.id.countDistinct().eq((long)categoryIds.size()));
		}
	}

	private JPAQuery<Social> applyPagination(JPAQuery<Social> query, Pageable pageable) {
		return query
			.orderBy(social.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize());
	}
}
