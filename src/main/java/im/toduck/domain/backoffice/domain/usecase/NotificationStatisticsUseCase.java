package im.toduck.domain.backoffice.domain.usecase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.backoffice.presentation.dto.response.NotificationStatisticsResponse;
import im.toduck.domain.notification.domain.service.NotificationService;
import im.toduck.domain.notification.persistence.entity.NotificationType;
import im.toduck.global.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class NotificationStatisticsUseCase {

	private final NotificationService notificationService;

	@Transactional(readOnly = true)
	public NotificationStatisticsResponse getNotificationStatistics() {
		LocalDate today = LocalDate.now();
		LocalDateTime startOfToday = today.atStartOfDay();
		LocalDateTime endOfToday = today.atTime(LocalTime.MAX);

		long totalNotificationsSent = notificationService.getTotalSentNotificationsCount();
		long todayNotificationsSent = notificationService.getSentNotificationsCountBetween(startOfToday, endOfToday);

		Map<NotificationType, Long> notificationCountsByType = notificationService.getSentNotificationCountsByType();

		log.info("백오피스 알림 통계 조회 - 전체 알림: {}, 오늘 알림: {}, 유형별 통계: {}개",
			totalNotificationsSent, todayNotificationsSent, notificationCountsByType.size());

		return NotificationStatisticsResponse.builder()
			.totalNotificationsSent(totalNotificationsSent)
			.todayNotificationsSent(todayNotificationsSent)
			.notificationCountsByType(notificationCountsByType)
			.build();
	}
}
