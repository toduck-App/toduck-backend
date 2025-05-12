package im.toduck.domain.notification.domain.usecase;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.notification.common.mapper.NotificationMapper;
import im.toduck.domain.notification.domain.service.DeviceTokenService;
import im.toduck.domain.notification.domain.service.NotificationService;
import im.toduck.domain.notification.domain.service.NotificationSettingService;
import im.toduck.domain.notification.persistence.entity.DeviceType;
import im.toduck.domain.notification.persistence.entity.Notification;
import im.toduck.domain.notification.persistence.entity.NotificationSetting;
import im.toduck.domain.notification.presentation.dto.request.NotificationSettingUpdateRequest;
import im.toduck.domain.notification.presentation.dto.response.NotificationListResponse;
import im.toduck.domain.notification.presentation.dto.response.NotificationSettingResponse;
import im.toduck.global.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class NotificationUseCase {

	private final DeviceTokenService deviceTokenService;
	private final NotificationSettingService notificationSettingService;
	private final NotificationService notificationService;

	@Transactional
	public void registerDeviceToken(final Long userId, final String token, final DeviceType deviceType) {
		deviceTokenService.registerDeviceToken(userId, token, deviceType);
		log.info("디바이스 토큰 등록 성공 - UserId: {}, DeviceType: {}", userId, deviceType);
	}

	@Transactional
	public void removeDeviceToken(final Long userId, final String token) {
		deviceTokenService.removeDeviceToken(userId, token);
		log.info("디바이스 토큰 삭제 성공 - UserId: {}", userId);
	}

	@Transactional(readOnly = true)
	public NotificationSettingResponse getNotificationSettings(final Long userId) {
		NotificationSetting settings = notificationSettingService.getOrCreateSettings(userId);
		return NotificationMapper.toNotificationSettingResponse(settings);
	}

	@Transactional
	public NotificationSettingResponse updateNotificationSettings(
		final Long userId,
		final NotificationSettingUpdateRequest request
	) {
		NotificationSetting settings = notificationSettingService.updateSettings(userId, request);
		log.info("알림 설정 업데이트 성공 - UserId: {}", userId);
		return NotificationMapper.toNotificationSettingResponse(settings);
	}

	@Transactional(readOnly = true)
	public NotificationListResponse getNotifications(final Long userId, final int page, final int size) {
		List<Notification> notifications = notificationService.getUserInAppNotifications(userId, page, size);
		return NotificationMapper.toNotificationListResponse(notifications);
	}

	@Transactional
	public void markNotificationAsRead(final Long userId, final Long notificationId) {
		notificationService.markAsRead(notificationId, userId);
		log.info("알림 읽음 표시 성공 - UserId: {}, NotificationId: {}", userId, notificationId);
	}

	@Transactional
	public void markAllNotificationsAsRead(final Long userId) {
		notificationService.markAllAsRead(userId);
		log.info("모든 알림 읽음 표시 성공 - UserId: {}", userId);
	}
}
