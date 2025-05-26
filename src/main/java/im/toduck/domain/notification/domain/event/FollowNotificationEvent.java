package im.toduck.domain.notification.domain.event;

import im.toduck.domain.notification.domain.data.FollowNotificationData;
import im.toduck.domain.notification.persistence.entity.NotificationType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FollowNotificationEvent extends NotificationEvent<FollowNotificationData> {

	private FollowNotificationEvent(Long userId, Long senderId, FollowNotificationData data) {
		super(userId, senderId, NotificationType.FOLLOW, data);
	}

	public static FollowNotificationEvent of(
		Long userId,
		Long senderId,
		String followerName
	) {
		return new FollowNotificationEvent(
			userId,
			senderId,
			FollowNotificationData.of(followerName)
		);
	}

	@Override
	public String getInAppTitle() {
		return getData().getFollowerName() + "님이 나를 팔로우 합니다.";
	}

	@Override
	public String getInAppBody() {
		return "";
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
		return "toduck://profile?userId=" + getSenderId();
	}
}
