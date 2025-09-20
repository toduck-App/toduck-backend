package im.toduck.domain.backoffice.common.mapper;

import java.time.LocalDateTime;
import java.util.List;

import im.toduck.domain.backoffice.persistence.entity.BroadcastNotification;
import im.toduck.domain.backoffice.persistence.entity.BroadcastNotificationStatus;
import im.toduck.domain.backoffice.presentation.dto.response.BroadcastNotificationListResponse;
import im.toduck.domain.backoffice.presentation.dto.response.BroadcastNotificationResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BroadcastNotificationMapper {

	public static BroadcastNotification toBroadcastNotification(
		final String title,
		final String message,
		final LocalDateTime scheduledAt,
		final int targetUserCount,
		final String jobKey
	) {
		return BroadcastNotification.builder()
			.title(title)
			.message(message)
			.scheduledAt(scheduledAt)
			.status(scheduledAt == null ? BroadcastNotificationStatus.SENDING : BroadcastNotificationStatus.SCHEDULED)
			.targetUserCount(targetUserCount)
			.jobKey(jobKey)
			.build();
	}

	public static BroadcastNotificationResponse toBroadcastNotificationResponse(
		final BroadcastNotification notification
	) {
		return BroadcastNotificationResponse.builder()
			.id(notification.getId())
			.title(notification.getTitle())
			.message(notification.getMessage())
			.scheduledAt(notification.getScheduledAt())
			.sentAt(notification.getSentAt())
			.status(notification.getStatus())
			.statusDescription(notification.getStatus().description())
			.targetUserCount(notification.getTargetUserCount())
			.sentUserCount(notification.getSentUserCount())
			.failureReason(notification.getFailureReason())
			.createdAt(notification.getCreatedAt())
			.canCancel(notification.canCancel())
			.build();
	}

	public static BroadcastNotificationListResponse toBroadcastNotificationListResponse(
		final List<BroadcastNotification> notifications
	) {
		List<BroadcastNotificationResponse> notificationResponses = notifications.stream()
			.map(BroadcastNotificationMapper::toBroadcastNotificationResponse)
			.toList();

		return BroadcastNotificationListResponse.builder()
			.notifications(notificationResponses)
			.totalCount(notifications.size())
			.build();
	}
}
