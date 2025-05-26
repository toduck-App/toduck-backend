package im.toduck.domain.notification.domain.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 내 댓글에 답글이 작성되었을 때 사용하는 알림 데이터
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReplyNotificationData extends AbstractNotificationData {
	private String replierName;
	private String replyContent;
	private Long postId;
	private Long commentId;

	public static ReplyNotificationData of(
		final String replierName,
		final String replyContent,
		final Long postId,
		final Long commentId
	) {
		return new ReplyNotificationData(replierName, replyContent, postId, commentId);
	}
}
