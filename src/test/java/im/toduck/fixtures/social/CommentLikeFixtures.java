package im.toduck.fixtures.social;

import im.toduck.domain.social.persistence.entity.Comment;
import im.toduck.domain.social.persistence.entity.CommentLike;
import im.toduck.domain.user.persistence.entity.User;

public class CommentLikeFixtures {

	/**
	 * CommentLike 엔티티를 생성합니다.
	 *
	 * @param user       좋아요를 누른 사용자
	 * @param comment    좋아요가 달린 댓글
	 * @return 생성된 CommentLike 엔티티
	 */
	public static CommentLike COMMENT_LIKE(User user, Comment comment) {
		return CommentLike.builder()
			.user(user)
			.comment(comment)
			.build();
	}
}
