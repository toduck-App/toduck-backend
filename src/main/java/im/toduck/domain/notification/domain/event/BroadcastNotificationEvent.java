package im.toduck.domain.notification.domain.event;

import im.toduck.domain.notification.domain.data.BroadcastNotificationData;
import im.toduck.domain.notification.persistence.entity.NotificationType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BroadcastNotificationEvent extends NotificationEvent<BroadcastNotificationData> {

	private BroadcastNotificationEvent(final Long userId, final BroadcastNotificationData data) {
		super(userId, NotificationType.BROADCAST, data);
	}

	public static BroadcastNotificationEvent of(final Long userId, final String title, final String message) {
		return new BroadcastNotificationEvent(
			userId,
			BroadcastNotificationData.of(title, message)
		);
	}

	@Override
	public String getInAppTitle() {
		return getData().getTitle();
	}

	@Override
	public String getInAppBody() {
		return getData().getMessage();
	}

	@Override
	public String getPushTitle() {
		return getData().getTitle();
	}

	@Override
	public String getPushBody() {
		return getData().getMessage();
	}

	@Override
	public String getActionUrl() {
		return "toduck://notification";
	}
}
