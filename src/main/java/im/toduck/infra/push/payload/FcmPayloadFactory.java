package im.toduck.infra.push.payload;

import java.util.Map;

import im.toduck.domain.notification.persistence.entity.Notification;
import im.toduck.domain.notification.persistence.entity.NotificationMethod;

/**
 * FCM 페이로드 생성을 위한 인터페이스
 */
public interface FcmPayloadFactory {
	/**
	 * FCM 메시지에 포함될 데이터를 생성합니다.
	 */
	Map<String, String> createPayload(Notification notification, NotificationMethod method);
}
