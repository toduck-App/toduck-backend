package im.toduck.domain.notification.domain.data;

import lombok.Getter;

/**
 * 알림 데이터의 기본 추상 클래스
 */
@Getter
public abstract class AbstractNotificationData implements NotificationData {
	// 공통 속성을 추가할 수 있음
	protected AbstractNotificationData() {
	}
}
