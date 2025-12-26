package im.toduck.domain.backoffice.domain.usecase;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.backoffice.common.mapper.BroadcastNotificationMapper;
import im.toduck.domain.backoffice.domain.event.BroadcastNotificationExecutionEvent;
import im.toduck.domain.backoffice.domain.service.BroadcastNotificationService;
import im.toduck.domain.backoffice.persistence.entity.BroadcastNotification;
import im.toduck.domain.backoffice.presentation.dto.request.BroadcastNotificationCreateRequest;
import im.toduck.domain.backoffice.presentation.dto.response.BroadcastNotificationListResponse;
import im.toduck.domain.backoffice.presentation.dto.response.BroadcastNotificationResponse;
import im.toduck.global.annotation.UseCase;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.quartz.auto-startup", havingValue = "true", matchIfMissing = true)
public class BroadcastNotificationUseCase {

	private final BroadcastNotificationService broadcastNotificationService;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional
	public BroadcastNotificationResponse createBroadcastNotification(final BroadcastNotificationCreateRequest request) {
		BroadcastNotification notification = broadcastNotificationService.createBroadcastNotification(
			request.title(),
			request.message(),
			request.scheduledAt(),
			request.actionUrl()
		);

		if (request.scheduledAt() == null) {
			// 즉시 발송 - 이벤트 발행
			BroadcastNotificationExecutionEvent event = new BroadcastNotificationExecutionEvent(
				notification.getId(), notification.getTitle(), notification.getMessage()
			);
			eventPublisher.publishEvent(event);
			log.info("브로드캐스트 알림 즉시 발송 이벤트 발행 - NotificationId: {}, Title: {}",
				notification.getId(), request.title());
		} else {
			log.info("브로드캐스트 알림 예약 생성 - NotificationId: {}, Title: {}, ScheduledAt: {}", notification.getId(),
				request.title(), request.scheduledAt());
		}

		return BroadcastNotificationMapper.toBroadcastNotificationResponse(notification);
	}

	@Transactional(readOnly = true)
	public BroadcastNotificationListResponse getAllBroadcastNotifications() {
		List<BroadcastNotification> notifications = broadcastNotificationService.getAllBroadcastNotifications();

		log.info("백오피스 브로드캐스트 알림 목록 조회 - 총 알림 수: {}", notifications.size());

		return BroadcastNotificationMapper.toBroadcastNotificationListResponse(notifications);
	}

	@Transactional
	public void cancelBroadcastNotification(final Long id) {
		BroadcastNotification notification = broadcastNotificationService.getBroadcastNotificationById(id)
			.orElseThrow(() -> CommonException.from(ExceptionCode.NOT_FOUND_NOTIFICATION));

		if (!notification.canCancel()) {
			throw CommonException.from(ExceptionCode.CANNOT_CANCEL_NOTIFICATION);
		}

		broadcastNotificationService.cancelScheduledNotification(id);

		log.info("브로드캐스트 알림 예약 취소 - NotificationId: {}, Title: {}",
			id, notification.getTitle());
	}
}
