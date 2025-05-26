package im.toduck.domain.notification.domain.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 내 댓글에 좋아요가 눌렸을 때 사용하는 알림 데이터
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LikeCommentNotificationData extends AbstractNotificationData {
	private String likerName;
	private Long postId;
	private Long commentId;

	public static LikeCommentNotificationData of(
		final String likerName,
		final Long postId,
		final Long commentId
	) {
		return new LikeCommentNotificationData(likerName, postId, commentId);
	}
}
