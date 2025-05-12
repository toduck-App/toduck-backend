package im.toduck.domain.notification.domain.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 게시글에 댓글이 작성되었을 때 사용하는 알림 데이터
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentNotificationData extends AbstractNotificationData {
	private final String commenterName;
	private final String commentContent;
	private final Long postId;

	public static CommentNotificationData of(
		final String commenterName,
		final String commentContent,
		final Long postId
	) {
		return new CommentNotificationData(commenterName, commentContent, postId);
	}
}
