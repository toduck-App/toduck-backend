package im.toduck.domain.social.common.mapper;

import im.toduck.domain.social.persistence.entity.Comment;
import im.toduck.domain.social.persistence.entity.CommentLike;
import im.toduck.domain.social.presentation.dto.response.CommentLikeCreateResponse;
import im.toduck.domain.social.presentation.dto.response.CommentLikeDto;
import im.toduck.domain.user.persistence.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentLikeMapper {

	public static CommentLike toCommentLike(final User user, final Comment comment) {
		return CommentLike.builder()
			.user(user)
			.comment(comment)
			.build();
	}

	public static CommentLikeCreateResponse toCommentLikeCreateResponse(final CommentLike commentLike) {
		return CommentLikeCreateResponse.builder()
			.commentLikeId(commentLike.getId())
			.build();
	}

	public static CommentLikeDto toCommentLikeDto(final Comment comment, final boolean isLiked) {
		return CommentLikeDto.builder()
			.isLiked(isLiked)
			.likeCount(comment.getLikeCount())
			.build();
	}
}
