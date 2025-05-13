package im.toduck.domain.notification.persistence.entity;

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
	COMMENT(NotificationCategory.SOCIAL, CommentNotificationData.class, "내 게시글에 댓글"),
	REPLY(NotificationCategory.SOCIAL, ReplyNotificationData.class, "내 댓글에 답글"),
	REPLY_ON_MY_POST(NotificationCategory.SOCIAL, ReplyOnMyPostNotificationData.class, "내 게시글의 내 댓글에 답글"),
	LIKE_POST(NotificationCategory.SOCIAL, LikePostNotificationData.class, "내 게시글에 좋아요"),
	LIKE_COMMENT(NotificationCategory.SOCIAL, LikeCommentNotificationData.class, "내 댓글 좋아요"),
	FOLLOW(NotificationCategory.SOCIAL, FollowNotificationData.class, "나를 팔로우"),
	ROUTINE_SHARE_MILESTONE(NotificationCategory.SOCIAL, RoutineShareMilestoneData.class, "루틴 공유 마일스톤"),

	SCHEDULE_REMINDER(NotificationCategory.HOME, ScheduleReminderData.class, "일정 임박", false),
	ROUTINE_REMINDER(NotificationCategory.HOME, RoutineReminderData.class, "루틴 임박", false),

	DIARY_REMINDER(NotificationCategory.DIARY, DiaryReminderData.class, "일기 작성 유도", false),
	INACTIVITY_REMINDER(NotificationCategory.NOTICE, InactivityReminderData.class, "미접속 알림", false);

	private final NotificationCategory category;
	private final Class<? extends NotificationData> dataClass;
	private final String description;
	private final boolean defaultInAppShown; // 앱 내 알림창에 표시할지 여부의 기본값

	NotificationType(
		NotificationCategory category,
		Class<? extends NotificationData> dataClass,
		String description,
		boolean defaultInAppShown
	) {
		this.category = category;
		this.dataClass = dataClass;
		this.description = description;
		this.defaultInAppShown = defaultInAppShown;
	}

	NotificationType(
		NotificationCategory category,
		Class<? extends NotificationData> dataClass,
		String description
	) {
		this.category = category;
		this.dataClass = dataClass;
		this.description = description;
		this.defaultInAppShown = true;
	}
}
