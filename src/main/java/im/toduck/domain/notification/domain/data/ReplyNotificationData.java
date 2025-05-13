package im.toduck.domain.notification.domain.data;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 내 댓글에 답글이 작성되었을 때 사용하는 알림 데이터
 */
@Getter
@NoArgsConstructor
public class ReplyNotificationData extends AbstractNotificationData {
	// TODO: 필요한 필드 추가
	// 예상 필드: 답글 작성자 ID, 답글 작성자 이름, 답글 내용, 원 댓글 ID, 게시글 ID 등
}
