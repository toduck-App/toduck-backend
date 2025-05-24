package im.toduck.domain.notification.persistence.repository.querydsl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import im.toduck.domain.notification.persistence.entity.Notification;
import im.toduck.domain.notification.persistence.entity.NotificationType;
import im.toduck.domain.notification.persistence.entity.QNotification;
import im.toduck.domain.user.persistence.entity.User;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryCustomImpl implements NotificationRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	private final QNotification qNotification = QNotification.notification;

	@Override
	public List<Notification> findRecentByUserAndType(User user, NotificationType type, int limit) {
		return queryFactory
			.selectFrom(qNotification)
			.where(
				qNotification.user.eq(user),
				qNotification.type.eq(type)
			)
			.orderBy(qNotification.createdAt.desc())
			.limit(limit)
			.fetch();
	}

	@Override
	public long countUnreadByUser(User user) {
		return queryFactory
			.select(qNotification.count())
			.from(qNotification)
			.where(
				qNotification.user.eq(user),
				qNotification.isRead.isFalse()
			)
			.fetchOne();
	}
}
