package im.toduck.infra.push;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;

import im.toduck.domain.notification.domain.service.DeviceTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {
	private final DeviceTokenService deviceTokenService;

	/**
	 * FCM을 통해 알림을 전송합니다.
	 *
	 * @param token 디바이스 토큰
	 * @param title 알림 제목
	 * @param body 알림 내용
	 * @param data 추가 데이터
	 * @return 알림 전송 결과
	 */
	public boolean sendNotification(String token, String title, String body, Map<String, String> data) {
		try {
			// FCM 메시지 구성
			Message message = Message.builder()
				.setToken(token)
				.setNotification(
					Notification.builder()
						.setTitle(title)
						.setBody(body)
						.build()
				)
				.putAllData(data)
				.setApnsConfig(getApnsConfig(data))
				.setAndroidConfig(getAndroidConfig())
				.build();

			String response = FirebaseMessaging.getInstance().sendAsync(message).get();
			log.info("FCM 알림 전송 성공 - token: {}, messageId: {}", token, response);
			return true;
		} catch (ExecutionException e) {
			if (e.getCause() instanceof FirebaseMessagingException fcmException) {
				if (fcmException.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
					log.warn("FCM 토큰 만료 감지 - 토큰: {}", token);
					deviceTokenService.removeInvalidToken(token);
					return false;
				}
			}
			log.error("FCM 알림 전송 실패 - token: {}", token, e);
			return false;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("FCM 알림 전송 중 인터럽트 - token: {}", token, e);
			return false;
		}
	}

	/**
	 * iOS용 알림 설정 생성
	 */
	private ApnsConfig getApnsConfig(Map<String, String> data) {
		String sound = data.get("sound");
		Map<String, Object> customData = new HashMap<>(data);
		customData.remove("sound");

		Aps.Builder apsBuilder = Aps.builder()
			.setContentAvailable(true)
			.setMutableContent(true);

		if (sound != null) {
			apsBuilder.setSound(sound);
		}

		return ApnsConfig.builder()
			.setAps(apsBuilder.build())
			.putAllCustomData(customData)
			.build();
	}

	/**
	 * Android용 알림 설정 생성
	 */
	private AndroidConfig getAndroidConfig() {
		return AndroidConfig.builder()
			.setPriority(AndroidConfig.Priority.HIGH)
			.setNotification(AndroidNotification.builder()
				.setSound("default")
				.setDefaultSound(true)
				.setDefaultVibrateTimings(true)
				.setDefaultLightSettings(true)
				.build())
			.build();
	}
}
