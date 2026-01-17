package im.toduck.domain.notification.domain.event;

import im.toduck.domain.notification.domain.data.BadgeAcquiredNotificationData;
import im.toduck.domain.notification.persistence.entity.NotificationType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BadgeAcquiredNotificationEvent extends NotificationEvent<BadgeAcquiredNotificationData> {

	private BadgeAcquiredNotificationEvent(final Long userId, final BadgeAcquiredNotificationData data) {
		super(userId, NotificationType.BADGE_ACQUIRED, data);
	}

	public static BadgeAcquiredNotificationEvent of(final Long userId, final BadgeAcquiredNotificationData data) {
		return new BadgeAcquiredNotificationEvent(userId, data);
	}

	@Override
	public String getInAppTitle() {
		return String.format("[%s] 뱃지를 획득했어요!", getData().getBadgeName());
	}

	@Override
	public String getInAppBody() {
		return "지금 바로 확인해보세요!";
	}

	@Override
	public String getPushTitle() {
		return getInAppTitle();
	}

	@Override
	public String getPushBody() {
		return getInAppBody();
	}

	@Override
	public String getActionUrl() {
		return "toduck://badge-history";
	}
}
