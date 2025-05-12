package im.toduck.domain.notification.domain.data;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자가 나를 팔로우했을 때 사용하는 알림 데이터
 */
@Getter
@NoArgsConstructor
public class FollowNotificationData extends AbstractNotificationData {
	// TODO: 필요한 필드 추가
	// 예상 필드: 팔로우한 사용자 ID, 사용자 이름 등
}
