package im.toduck.domain.notification.domain.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import im.toduck.domain.notification.persistence.entity.DeviceToken;
import im.toduck.domain.notification.persistence.entity.Notification;
import im.toduck.domain.notification.persistence.entity.NotificationMethod;
import im.toduck.domain.notification.persistence.entity.NotificationSetting;
import im.toduck.infra.push.FcmService;
import im.toduck.infra.push.payload.FcmPayloadFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushNotificationService {
	private final DeviceTokenService deviceTokenService;
	private final NotificationSettingService notificationSettingService;
	private final NotificationService notificationService;
	private final FcmService fcmService;
	private final FcmPayloadFactory fcmPayloadFactory;

	@Transactional
	public void sendPushNotification(final Notification notification) {
		Long userId = notification.getUser().getId();

		List<DeviceToken> deviceTokens = deviceTokenService.getUserDeviceTokens(notification.getUser());

		if (deviceTokens.isEmpty()) {
			log.info("등록된 디바이스 토큰 없음 - 사용자: {}", userId);
			return;
		}

		NotificationSetting settings = notificationSettingService.getOrCreateSettings(userId);
		NotificationMethod method = settings.getNotificationMethod();

		Map<String, String> payload = fcmPayloadFactory.createPayload(notification, method);

		boolean anySuccess = false;
		for (DeviceToken deviceToken : deviceTokens) {
			boolean success = fcmService.sendNotification(
				deviceToken.getToken(),
				notification.getPushTitle(),
				notification.getPushBody(),
				payload
			);

			if (success) {
				anySuccess = true;
			} else {
				log.warn("알림 전송 실패 - 토큰: {}", deviceToken.getToken());
			}
		}

		if (anySuccess) {
			notificationService.markAsSent(notification);
		}
	}
}
