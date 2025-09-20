package im.toduck.domain.notification.persistence.repository.querydsl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import im.toduck.domain.notification.persistence.entity.Notification;
import im.toduck.domain.notification.persistence.entity.NotificationType;
import im.toduck.domain.user.persistence.entity.User;

public interface NotificationRepositoryCustom {
	List<Notification> findRecentByUserAndType(User user, NotificationType type, int limit);

	long countUnreadByUser(User user);

	long countSentNotifications();

	long countSentNotificationsBetween(final LocalDateTime startDateTime, final LocalDateTime endDateTime);

	Map<NotificationType, Long> countSentNotificationsByType();
}
