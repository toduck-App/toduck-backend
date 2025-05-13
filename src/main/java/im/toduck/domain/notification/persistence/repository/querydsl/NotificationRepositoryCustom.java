package im.toduck.domain.notification.persistence.repository.querydsl;

import java.util.List;

import im.toduck.domain.notification.persistence.entity.Notification;
import im.toduck.domain.notification.persistence.entity.NotificationType;
import im.toduck.domain.user.persistence.entity.User;

public interface NotificationRepositoryCustom {
	List<Notification> findRecentByUserAndType(User user, NotificationType type, int limit);

	long countUnreadByUser(User user);
}
