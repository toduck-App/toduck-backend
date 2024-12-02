package im.toduck.domain.social.common.mapper;

import im.toduck.domain.social.persistence.entity.Comment;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.vo.CommentContent;
import im.toduck.domain.social.presentation.dto.request.CommentCreateRequest;
import im.toduck.domain.social.presentation.dto.response.CommentCreateResponse;
import im.toduck.domain.social.presentation.dto.response.CommentDto;
import im.toduck.domain.social.presentation.dto.response.CommentLikeDto;
import im.toduck.domain.social.presentation.dto.response.OwnerDto;
import im.toduck.domain.user.persistence.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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

	public static CommentDto toCommentDto(Comment comment, boolean isCommentLiked) {
		return CommentDto.builder()
			.commentId(comment.getId())
			.owner(getOwner(comment.getUser()))
			.content(comment.getContent().getValue())
			.commentLikeInfo(getCommentLikeDto(comment, isCommentLiked))
			.createdAt(comment.getCreatedAt())
			.build();
	}

	private static CommentLikeDto getCommentLikeDto(Comment comment, boolean isCommentLiked) {
		return CommentLikeMapper.toCommentLikeDto(comment, isCommentLiked);
	}

	private static OwnerDto getOwner(User user) {
		return OwnerDto.builder()
			.ownerId(user.getId())
			.nickname(user.getNickname())
			.build();
	}
}
