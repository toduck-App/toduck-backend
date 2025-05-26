package im.toduck.domain.notification.domain.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 내 게시글에 좋아요가 눌렸을 때 사용하는 알림 데이터
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LikePostNotificationData extends AbstractNotificationData {
	private String likerName;
	private Long postId;

	public static LikePostNotificationData of(
		final String likerName,
		final Long postId
	) {
		return new LikePostNotificationData(likerName, postId);
	}
}
