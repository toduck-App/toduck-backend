package im.toduck.domain.social.mapper;

import im.toduck.domain.social.persistence.entity.Comment;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.presentation.dto.request.CommentCreateRequest;
import im.toduck.domain.social.presentation.dto.response.CommentCreateResponse;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.annotation.Mapper;

@Mapper
public class CommentMapper {
	public static Comment toComment(User user, Social socialBoard, CommentCreateRequest request) {
		return Comment.builder()
			.user(user)
			.social(socialBoard)
			.content(request.content())
			.build();
	}

	public static CommentCreateResponse toCommentCreateResponse(Comment comment) {
		return CommentCreateResponse.builder()
			.socialCommentId(comment.getId())
			.build();
	}
}
