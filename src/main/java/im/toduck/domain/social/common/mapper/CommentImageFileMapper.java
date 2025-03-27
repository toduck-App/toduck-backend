package im.toduck.domain.social.common.mapper;

import im.toduck.domain.social.persistence.entity.Comment;
import im.toduck.domain.social.persistence.entity.CommentImageFile;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentImageFileMapper {
	public static CommentImageFile toCommentImageFile(final Comment comment, final String url) {
		return CommentImageFile.builder()
			.comment(comment)
			.url(url)
			.build();
	}
}
