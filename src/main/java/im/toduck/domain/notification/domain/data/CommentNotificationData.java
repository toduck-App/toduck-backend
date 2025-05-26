package im.toduck.domain.notification.domain.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시글에 댓글이 작성되었을 때 사용하는 알림 데이터
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentNotificationData extends AbstractNotificationData {
	private String commenterName;
	private String commentContent;
	private Long postId;
	private Long commentId;

	public static CommentNotificationData of(
		final String commenterName,
		final String commentContent,
		final Long postId,
		final Long commentId
	) {
		return new CommentNotificationData(commenterName, commentContent, postId, commentId);
	}
}
