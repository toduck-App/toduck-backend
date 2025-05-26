package im.toduck.domain.notification.domain.event;

import im.toduck.domain.notification.domain.data.ReplyOnMyPostNotificationData;
import im.toduck.domain.notification.persistence.entity.NotificationType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReplyOnMyPostNotificationEvent extends NotificationEvent<ReplyOnMyPostNotificationData> {

	private ReplyOnMyPostNotificationEvent(Long userId, Long senderId, ReplyOnMyPostNotificationData data) {
		super(userId, senderId, NotificationType.REPLY_ON_MY_POST, data);
	}

	public static ReplyOnMyPostNotificationEvent of(
		Long userId,
		Long senderId,
		String replierName,
		String replyContent,
		Long postId,
		Long commentId
	) {
		return new ReplyOnMyPostNotificationEvent(
			userId,
			senderId,
			ReplyOnMyPostNotificationData.of(replierName, replyContent, postId, commentId)
		);
	}

	@Override
	public String getInAppTitle() {
		return getData().getReplierName() + "님이 내 게시글에 대댓글을 남겼어요.";
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
