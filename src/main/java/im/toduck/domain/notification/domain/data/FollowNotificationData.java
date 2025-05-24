package im.toduck.domain.notification.domain.data;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자가 나를 팔로우했을 때 사용하는 알림 데이터
 */
@Getter
@NoArgsConstructor
public class FollowNotificationData extends AbstractNotificationData {
	private String followerName;

	private FollowNotificationData(String followerName) {
		this.followerName = followerName;
	}

	public static FollowNotificationData of(String followerName) {
		return new FollowNotificationData(followerName);
	}
}
