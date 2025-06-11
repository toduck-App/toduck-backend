package im.toduck.domain.notification.domain.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 일기 작성 유도 알림에 사용하는 데이터
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DiaryReminderData extends AbstractNotificationData {
	public static DiaryReminderData create() {
		return new DiaryReminderData();
	}
}
