package im.toduck.domain.notification.domain.data;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 미접속 알림에 사용하는 데이터
 */
@Getter
@NoArgsConstructor
public class InactivityReminderData extends AbstractNotificationData {
	// TODO: 필요한 필드 추가
	// 예상 필드: 마지막 접속 날짜, 미접속 일수 등
}
