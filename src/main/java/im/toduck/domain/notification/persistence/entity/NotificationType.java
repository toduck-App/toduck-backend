package im.toduck.domain.notification.persistence.entity;

import im.toduck.domain.notification.domain.data.BroadcastNotificationData;
import im.toduck.domain.notification.domain.data.CommentNotificationData;
import im.toduck.domain.notification.domain.data.DiaryReminderData;
import im.toduck.domain.notification.domain.data.FollowNotificationData;
import im.toduck.domain.notification.domain.data.InactivityReminderData;
import im.toduck.domain.notification.domain.data.LikeCommentNotificationData;
import im.toduck.domain.notification.domain.data.LikePostNotificationData;
import im.toduck.domain.notification.domain.data.NotificationData;
import im.toduck.domain.notification.domain.data.ReplyNotificationData;
import im.toduck.domain.notification.domain.data.ReplyOnMyPostNotificationData;
import im.toduck.domain.notification.domain.data.RoutineReminderData;
import im.toduck.domain.notification.domain.data.RoutineShareMilestoneData;
import im.toduck.domain.notification.domain.data.ScheduleReminderData;
import lombok.Getter;

@Getter
public enum NotificationType {
	COMMENT(NotificationCategory.SOCIAL, CommentNotificationData.class, "내 게시글에 댓글", Priority.NORMAL),
	REPLY(NotificationCategory.SOCIAL, ReplyNotificationData.class, "내 댓글에 답글", Priority.NORMAL),
	REPLY_ON_MY_POST(
		NotificationCategory.SOCIAL, ReplyOnMyPostNotificationData.class, "내 게시글의 내 댓글에 답글", Priority.NORMAL
	),
	LIKE_POST(NotificationCategory.SOCIAL, LikePostNotificationData.class, "내 게시글에 좋아요", Priority.NORMAL),
	LIKE_COMMENT(NotificationCategory.SOCIAL, LikeCommentNotificationData.class, "내 댓글 좋아요", Priority.NORMAL),
	FOLLOW(NotificationCategory.SOCIAL, FollowNotificationData.class, "나를 팔로우", Priority.NORMAL),
	ROUTINE_SHARE_MILESTONE(
		NotificationCategory.SOCIAL, RoutineShareMilestoneData.class, "루틴 공유 마일스톤", Priority.NORMAL
	),

	SCHEDULE_REMINDER(NotificationCategory.HOME, ScheduleReminderData.class, "일정 임박", Priority.HIGH, false),
	ROUTINE_REMINDER(NotificationCategory.HOME, RoutineReminderData.class, "루틴 임박", Priority.HIGH, false),

	DIARY_REMINDER(NotificationCategory.DIARY, DiaryReminderData.class, "일기 작성 유도", Priority.NORMAL, false),
	INACTIVITY_REMINDER(NotificationCategory.NOTICE, InactivityReminderData.class, "미접속 알림", Priority.LOW, false),

	BROADCAST(NotificationCategory.NOTICE, BroadcastNotificationData.class, "공지사항", Priority.HIGH, false);

	private final NotificationCategory category;
	private final Class<? extends NotificationData> dataClass;
	private final String description;
	private final Priority priority; // 알림 우선순위
	private final boolean defaultInAppShown; // 앱 내 알림창에 표시할지 여부의 기본값

	NotificationType(
		NotificationCategory category,
		Class<? extends NotificationData> dataClass,
		String description,
		Priority priority,
		boolean defaultInAppShown
	) {
		this.category = category;
		this.dataClass = dataClass;
		this.description = description;
		this.priority = priority;
		this.defaultInAppShown = defaultInAppShown;
	}

	NotificationType(
		NotificationCategory category,
		Class<? extends NotificationData> dataClass,
		String description,
		Priority priority
	) {
		this.category = category;
		this.dataClass = dataClass;
		this.description = description;
		this.defaultInAppShown = true;
		this.priority = priority;
	}

	@Getter
	public enum Priority {
		LOW(1),
		NORMAL(5),
		HIGH(8),
		URGENT(10);

		private final int level;

		Priority(int level) {
			this.level = level;
		}
	}
}
