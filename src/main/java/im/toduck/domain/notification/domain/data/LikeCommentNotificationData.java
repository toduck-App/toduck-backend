package im.toduck.domain.notification.domain.data;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 내 댓글에 좋아요가 눌렸을 때 사용하는 알림 데이터
 */
@Getter
@NoArgsConstructor
public class LikeCommentNotificationData extends AbstractNotificationData {
	// TODO: 필요한 필드 추가
	// 예상 필드: 좋아요 누른 사용자 ID, 사용자 이름, 댓글 ID, 게시글 ID 등
}
