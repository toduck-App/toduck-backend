package im.toduck.domain.social.mapper;

import im.toduck.domain.social.persistence.entity.Comment;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.vo.CommentContent;
import im.toduck.domain.social.presentation.dto.request.CommentCreateRequest;
import im.toduck.domain.social.presentation.dto.response.CommentCreateResponse;
import im.toduck.domain.social.presentation.dto.response.CommentDto;
import im.toduck.domain.social.presentation.dto.response.OwnerDto;
import im.toduck.domain.user.persistence.entity.User;

public class CommentMapper {
	public static Comment toComment(User user, Social socialBoard, CommentCreateRequest request) {
		return Comment.builder()
			.user(user)
			.social(socialBoard)
			.content(CommentContent.from(request.content()))
			.build();
	}

	public static CommentCreateResponse toCommentCreateResponse(Comment comment) {
		return CommentCreateResponse.builder()
			.commentId(comment.getId())
			.build();
	}

	public static CommentDto toCommentDto(Comment comment) {
		return CommentDto.builder()
			.commentId(comment.getId())
			.owner(getOwner(comment.getUser()))
			.content(comment.getContent().getValue())
			.createdAt(comment.getCreatedAt())
			.build();
	}

	private static OwnerDto getOwner(User user) {
		return OwnerDto.builder()
			.id(user.getId())
			.nickname(user.getNickname())
			.build();
	}
}
