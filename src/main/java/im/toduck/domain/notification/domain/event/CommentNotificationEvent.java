package im.toduck.domain.notification.domain.event;

import im.toduck.domain.notification.domain.data.CommentNotificationData;
import im.toduck.domain.notification.persistence.entity.NotificationType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentNotificationEvent extends NotificationEvent<CommentNotificationData> {

	private CommentNotificationEvent(Long userId, Long senderId, CommentNotificationData data) {
		super(userId, senderId, NotificationType.COMMENT, data);
	}

	public static CommentNotificationEvent of(
		Long userId,
		Long senderId,
		String commenterName,
		String commentContent,
		Long postId,
		Long commentId
	) {
		return new CommentNotificationEvent(
			userId,
			senderId,
			CommentNotificationData.of(commenterName, commentContent, postId, commentId)
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
		return "toduck://post?postId=" + getData().getPostId() + "&commentId=" + getData().getCommentId();
	}
}
