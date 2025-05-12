package im.toduck.domain.notification.domain.data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * 모든 알림 데이터 클래스가 구현해야 하는 기본 인터페이스
 */
@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME,
	property = "type"
)
public interface NotificationData extends Serializable {
}
