package im.toduck.domain.notification.domain.event;

import im.toduck.domain.notification.domain.data.DiaryReminderData;
import im.toduck.domain.notification.persistence.entity.NotificationType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DiaryReminderNotificationEvent extends NotificationEvent<DiaryReminderData> {

	private DiaryReminderNotificationEvent(Long userId, DiaryReminderData data) {
		super(userId, NotificationType.DIARY_REMINDER, data);
	}

	public static DiaryReminderNotificationEvent of(Long userId) {
		return new DiaryReminderNotificationEvent(
			userId,
			DiaryReminderData.create()
		);
	}

	@Override
	public String getInAppTitle() {
		return "";
	}

	@Override
	public String getInAppBody() {
		return "";
	}

	@Override
	public String getPushTitle() {
		return "일기로 오늘 하루를 기록해요.";
	}

	@Override
	public String getPushBody() {
		return "기분과 성과를 기록하며 하루를 돌아보세요.";
	}

	@Override
	public String getActionUrl() {
		return "toduck://diary";
	}
}
