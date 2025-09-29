package im.toduck.domain.notification.persistence.repository.querydsl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
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

	@Override
	public long countSentNotifications() {
		Long count = queryFactory
			.select(qNotification.count())
			.from(qNotification)
			.where(qNotification.isSent.isTrue())
			.fetchOne();

		return count != null ? count : 0L;
	}

	@Override
	public long countSentNotificationsBetween(final LocalDateTime startDateTime, final LocalDateTime endDateTime) {
		Long count = queryFactory
			.select(qNotification.count())
			.from(qNotification)
			.where(
				qNotification.isSent.isTrue(),
				createdAtBetween(startDateTime, endDateTime)
			)
			.fetchOne();

		return count != null ? count : 0L;
	}

	@Override
	public Map<NotificationType, Long> countSentNotificationsByType() {
		List<Tuple> results = queryFactory
			.select(qNotification.type, qNotification.count())
			.from(qNotification)
			.where(qNotification.isSent.isTrue())
			.groupBy(qNotification.type)
			.fetch();

		return results.stream()
			.collect(java.util.stream.Collectors.toMap(
				tuple -> tuple.get(qNotification.type),
				tuple -> tuple.get(qNotification.count())
			));
	}

	private BooleanExpression createdAtBetween(final LocalDateTime startDateTime, final LocalDateTime endDateTime) {
		return qNotification.createdAt.between(startDateTime, endDateTime);
	}
}
