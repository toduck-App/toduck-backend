package im.toduck.infra.push.payload;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import im.toduck.domain.notification.persistence.entity.Notification;
import im.toduck.domain.notification.persistence.entity.NotificationMethod;

/**
 * 기본 FCM 페이로드 생성 구현체
 */
@Component
public class DefaultFcmPayloadFactory implements FcmPayloadFactory {

	@Override
	public Map<String, String> createPayload(final Notification notification, final NotificationMethod method) {
		Map<String, String> data = new HashMap<>();

		data.put("notificationType", notification.getType().name());
		data.put("notificationId", notification.getId().toString());
		data.put("actionUrl", notification.getActionUrl() != null ? notification.getActionUrl() : "");
		data.put("sound", getSoundSetting(method));

		return data;
	}

	private String getSoundSetting(final NotificationMethod method) {
		return switch (method) {
			case SOUND_ONLY -> "default";
			case VIBRATION_ONLY -> null;
		};
	}
}
