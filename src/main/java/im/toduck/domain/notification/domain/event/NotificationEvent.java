package im.toduck.domain.notification.domain.event;

import java.io.Serializable;
import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import im.toduck.domain.notification.domain.data.NotificationData;
import im.toduck.domain.notification.persistence.entity.NotificationType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 알림 이벤트의 기본 추상 클래스.
 * 모든 알림 이벤트는 이 클래스를 상속받아 구현합니다.
 *
 * @param <T> 알림에 필요한 추가 데이터 타입 (NotificationData 상속)
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class NotificationEvent<T extends NotificationData> implements Serializable {
	/**
	 * 알림을 받을 사용자의 ID
	 */
	private Long userId;

	/**
	 * 알림 전송을 트리거하는 사용자의 ID, 전송자가 없는 경우 null
	 */
	@Nullable
	private Long senderId;

	@JsonIgnore
	public Optional<Long> getOptionalSenderId() {
		return Optional.ofNullable(senderId);
	}

	/**
	 * 알림의 유형
	 */
	private NotificationType type;

	/**
	 * 알림 유형별 구체적인 데이터
	 */
	private T data;

	/**
	 * 알림 이벤트 생성자
	 *
	 * @param userId 알림을 받을 사용자의 ID
	 * @param type 알림 유형
	 * @param data 알림 관련 추가 데이터
	 */
	protected NotificationEvent(final Long userId, final NotificationType type, final T data) {
		this.userId = userId;
		this.senderId = null;
		this.type = type;
		this.data = data;
	}

	/**
	 * 알림 이벤트 생성자
	 *
	 * @param userId 알림을 받을 사용자의 ID
	 * @param senderId 알림을 받을 사용자의 ID
	 * @param type 알림 유형
	 * @param data 알림 관련 추가 데이터
	 */
	protected NotificationEvent(final Long userId, final Long senderId, final NotificationType type, final T data) {
		this.userId = userId;
		this.senderId = senderId;
		this.type = type;
		this.data = data;
	}

	/**
	 * 앱 내 알림창에 표시될 제목을 반환합니다.
	 *
	 * @return 앱 내 알림 제목
	 */
	public abstract String getInAppTitle();

	/**
	 * 앱 내 알림창에 표시될 내용을 반환합니다.
	 *
	 * @return 앱 내 알림 내용
	 */
	public abstract String getInAppBody();

	/**
	 * 푸시 알림으로 전송될 제목을 반환합니다.
	 *
	 * @return 푸시 알림 제목
	 */
	public abstract String getPushTitle();

	/**
	 * 푸시 알림으로 전송될 내용을 반환합니다.
	 *
	 * @return 푸시 알림 내용
	 */
	public abstract String getPushBody();

	/**
	 * 알림 클릭 시 이동할 앱 내 경로(URL)를 반환합니다.
	 *
	 * @return 액션 URL (앱 내 화면 이동 경로)
	 */
	public abstract String getActionUrl();
}
