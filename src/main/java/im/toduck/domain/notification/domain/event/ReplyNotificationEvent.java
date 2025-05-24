package im.toduck.domain.notification.domain.event;

import im.toduck.domain.notification.domain.data.ReplyNotificationData;
import im.toduck.domain.notification.persistence.entity.NotificationType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReplyNotificationEvent extends NotificationEvent<ReplyNotificationData> {

	private ReplyNotificationEvent(Long userId, Long senderId, ReplyNotificationData data) {
		super(userId, senderId, NotificationType.REPLY, data);
	}

	public static ReplyNotificationEvent of(
		Long userId,
		Long senderId,
		String replierName,
		String replyContent,
		Long postId,
		Long commentId
	) {
		return new ReplyNotificationEvent(
			userId,
			senderId,
			ReplyNotificationData.of(replierName, replyContent, postId, commentId)
		);
	}

	@Override
	public String getInAppTitle() {
		return getData().getReplierName() + "님이 대댓글을 남겼어요.";
	}

	@Override
	public String getInAppBody() {
		return getData().getReplyContent();
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
		return "toduck://post?postId=" + getData().getPostId() + "&commentId=" + getData().getCommentId();
	}
}
