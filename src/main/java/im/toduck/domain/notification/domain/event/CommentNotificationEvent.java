package im.toduck.domain.notification.domain.event;

import im.toduck.domain.notification.domain.data.CommentNotificationData;
import im.toduck.domain.notification.persistence.entity.NotificationType;
import lombok.Getter;

@Getter
public class CommentNotificationEvent extends NotificationEvent<CommentNotificationData> {

	private CommentNotificationEvent(Long userId, CommentNotificationData data) {
		super(userId, NotificationType.COMMENT, data);
	}

	public static CommentNotificationEvent of(Long userId, String commenterName, String commentContent, Long postId) {
		return new CommentNotificationEvent(
			userId,
			CommentNotificationData.of(commenterName, commentContent, postId)
		);
	}

	@Override
	public String getInAppTitle() {
		return getData().getCommenterName() + "님이 내 게시물에 댓글을 남겼어요.";
	}

	@Override
	public String getInAppBody() {
		return getData().getCommentContent();
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
		return "toduck://post?postId=" + getData().getPostId();
	}
}
