package im.toduck.domain.notification.common.serializer;

import java.util.Arrays;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;

import im.toduck.domain.notification.persistence.entity.NotificationType;

@Component
public class NotificationMapperFactory {

	/**
	 * 알림 데이터 처리를 위한 공통 ObjectMapper를 생성합니다.
	 * 이 메서드는 데이터베이스 컨버터와 메시지 브로커에서 동일하게 사용됩니다.
	 *
	 * @return 다형성 객체 변환을 위해 설정된 ObjectMapper
	 */
	public static ObjectMapper createObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();

		Arrays.stream(NotificationType.values())
			.filter(type -> type.getDataClass() != null)
			.forEach(type -> objectMapper.registerSubtypes(
				new NamedType(type.getDataClass(), type.name())
			));

		objectMapper.activateDefaultTyping(
			objectMapper.getPolymorphicTypeValidator(),
			ObjectMapper.DefaultTyping.NON_FINAL,
			JsonTypeInfo.As.PROPERTY
		);

		return objectMapper;
	}
}
