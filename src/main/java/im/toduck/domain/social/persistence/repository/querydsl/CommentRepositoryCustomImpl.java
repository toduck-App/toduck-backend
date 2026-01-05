package im.toduck.domain.social.persistence.repository.querydsl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import im.toduck.domain.mypage.presentation.dto.response.MyCommentsResponse;
import im.toduck.domain.social.persistence.entity.QComment;
import im.toduck.domain.social.persistence.entity.QCommentImageFile;
import im.toduck.domain.social.persistence.entity.QCommentLike;
import im.toduck.domain.social.presentation.dto.response.CommentDto;
import im.toduck.domain.social.presentation.dto.response.CommentLikeDto;
import im.toduck.domain.social.presentation.dto.response.OwnerDto;
import im.toduck.domain.user.persistence.entity.QUser;
import im.toduck.global.persistence.helper.DailyCountQueryHelper;
import im.toduck.global.persistence.projection.DailyCount;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QComment qComment = QComment.comment;
	private final QUser qUser = QUser.user;
	private final QCommentImageFile qImage = QCommentImageFile.commentImageFile;
	private final QCommentLike qLike = QCommentLike.commentLike;

	@Override
	public List<MyCommentsResponse> findMyCommentsWithProjection(Long userId,
		Long cursor,
		Pageable pageable) {

		return queryFactory
			.select(Projections.constructor(MyCommentsResponse.class,
				qComment.social.id,

				Projections.constructor(CommentDto.class,
					qComment.id,
					qComment.parent.id,
					Projections.constructor(OwnerDto.class,
						qUser.id,
						qUser.nickname,
						qUser.imageUrl
					),
					qImage.id.isNotNull(),
					qImage.url,
					qComment.content.value,
					commentLikeProjection(userId),
					qComment.parent.isNotNull(),
					qComment.createdAt
				)
			))
			.from(qComment)
			.leftJoin(qUser).on(qUser.id.eq(qComment.user.id))
			.leftJoin(qImage).on(qImage.comment.id.eq(qComment.id))
			.where(
				qComment.user.id.eq(userId),
				qComment.deletedAt.isNull(),
				cursorCondition(cursor)
			)
			.orderBy(qComment.id.desc())
			.limit(pageable.getPageSize())
			.fetch();
	}

	private BooleanExpression cursorCondition(Long cursor) {
		return cursor != null ? qComment.id.lt(cursor) : null;
	}

	private Expression<CommentLikeDto> commentLikeProjection(Long userId) {
		return Projections.constructor(CommentLikeDto.class,
			JPAExpressions
				.selectOne()
				.from(qLike)
				.where(
					qLike.comment.id.eq(qComment.id),
					qLike.user.id.eq(userId)
				)
				.exists(),
			qComment.likeCount
		);
	}

	@Override
	public List<DailyCount> countByCreatedAtBetweenGroupByDate(
		final LocalDateTime startDateTime,
		final LocalDateTime endDateTime
	) {
		return DailyCountQueryHelper.countGroupByDate(
			queryFactory, qComment, qComment.createdAt, qComment.count(), startDateTime, endDateTime
		);
	}
}
