package im.toduck.fixtures.social;

import im.toduck.domain.social.persistence.entity.Comment;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.vo.CommentContent;
import im.toduck.domain.user.persistence.entity.User;

public class CommentFixtures {

	/**
	 * 기본 댓글 content 값
	 */
	public static final String DEFAULT_COMMENT_CONTENT = "Test comment";

	/**
	 * 단일 Comment 엔티티를 생성
	 *
	 * @param user   댓글 작성자
	 * @param social 댓글이 속한 게시글
	 * @return 생성된 Comment 엔티티
	 */
	public static Comment SINGLE_COMMENT(User user, Social social) {
		return Comment.builder()
			.user(user)
			.social(social)
			.content(CommentContent.from(DEFAULT_COMMENT_CONTENT))
			.build();
	}
}
