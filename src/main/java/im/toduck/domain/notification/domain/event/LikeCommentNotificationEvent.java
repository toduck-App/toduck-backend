package im.toduck.domain.notification.domain.event;

import im.toduck.domain.notification.domain.data.LikeCommentNotificationData;
import im.toduck.domain.notification.persistence.entity.NotificationType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LikeCommentNotificationEvent extends NotificationEvent<LikeCommentNotificationData> {

	private LikeCommentNotificationEvent(Long userId, Long senderId, LikeCommentNotificationData data) {
		super(userId, senderId, NotificationType.LIKE_COMMENT, data);
	}

	public static LikeCommentNotificationEvent of(
		Long userId,
		Long senderId,
		String likerName,
		Long postId,
		Long commentId
	) {
		return new LikeCommentNotificationEvent(
			userId,
			senderId,
			LikeCommentNotificationData.of(likerName, postId, commentId)
		);
	}

	@Override
	public String getInAppTitle() {
		return getData().getLikerName() + "님이 내 댓글에 좋아요를 남겼어요.";
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
		return "toduck://post?postId=" + getData().getPostId() + "&commentId=" + getData().getCommentId();
	}
}
