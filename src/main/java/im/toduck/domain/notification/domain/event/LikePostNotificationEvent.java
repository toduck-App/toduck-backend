package im.toduck.domain.notification.domain.event;

import im.toduck.domain.notification.domain.data.LikePostNotificationData;
import im.toduck.domain.notification.persistence.entity.NotificationType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LikePostNotificationEvent extends NotificationEvent<LikePostNotificationData> {

	private LikePostNotificationEvent(Long userId, Long senderId, LikePostNotificationData data) {
		super(userId, senderId, NotificationType.LIKE_POST, data);
	}

	public static LikePostNotificationEvent of(
		Long userId,
		Long senderId,
		String likerName,
		Long postId
	) {
		return new LikePostNotificationEvent(
			userId,
			senderId,
			LikePostNotificationData.of(likerName, postId)
		);
	}

	@Override
	public String getInAppTitle() {
		return getData().getLikerName() + "님이 내 게시글에 좋아요를 남겼어요.";
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
		return getPushTitle();
	}

	@Override
	public String getActionUrl() {
		return "toduck://post?postId=" + getData().getPostId();
	}
}
