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
	private static final long BLOCKED_USER_ID = 0L;
	private static final String BLOCKED_USER_NICKNAME = "차단된 사용자";
	private static final String BLOCKED_MESSAGE_CONTENT = "차단한 작성자의 댓글입니다.";

	public static Comment toComment(
		final User user,
		final Social socialBoard,
		final Comment parentComment,
		final CommentCreateRequest request
	) {
		return Comment.builder()
			.user(user)
			.social(socialBoard)
			.parent(parentComment)
			.content(CommentContent.from(request.content()))
			.build();
	}

	public static CommentCreateResponse toCommentCreateResponse(final Comment comment) {
		return CommentCreateResponse.builder()
			.commentId(comment.getId())
			.build();
	}

	public static CommentDto toCommentDto(
		final Comment comment,
		final boolean isCommentLiked,
		final boolean isBlocked
	) {
		return CommentDto.builder()
			.commentId(comment.getId())
			.parentCommentId(getParentCommentId(comment))
			.owner(getOwner(comment, isBlocked))
			.content(getContent(comment, isBlocked))
			.commentLikeInfo(getCommentLikeDto(comment, isCommentLiked))
			.isReply(isReply(comment))
			.createdAt(comment.getCreatedAt())
			.build();
	}

	private static Long getParentCommentId(final Comment comment) {
		if (comment.getParent() == null) {
			return null;
		}
		return comment.getParent().getId();
	}

	private static OwnerDto getOwner(final Comment comment, final boolean isBlocked) {
		if (isBlocked) {
			return OwnerDto.builder()
				.ownerId(BLOCKED_USER_ID)
				.nickname(BLOCKED_USER_NICKNAME)
				.build();
		}

		return OwnerDto.builder()
			.ownerId(comment.getUser().getId())
			.nickname(comment.getUser().getNickname())
			.build();
	}

	private static String getContent(final Comment comment, final boolean isBlocked) {
		if (isBlocked) {
			return BLOCKED_MESSAGE_CONTENT;
		}
		return comment.getContent().getValue();
	}

	private static CommentLikeDto getCommentLikeDto(final Comment comment, final boolean isCommentLiked) {
		return CommentLikeMapper.toCommentLikeDto(comment, isCommentLiked);
	}

	private static boolean isReply(final Comment comment) {
		return comment.getParent() != null;
	}
}
