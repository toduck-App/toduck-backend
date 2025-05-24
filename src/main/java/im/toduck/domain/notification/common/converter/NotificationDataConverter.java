package im.toduck.domain.notification.common.converter;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import im.toduck.domain.notification.common.serializer.NotificationMapperFactory;
import im.toduck.domain.notification.domain.data.NotificationData;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter
public class NotificationDataConverter implements AttributeConverter<NotificationData, String> {
	private static final ObjectMapper objectMapper = NotificationMapperFactory.createObjectMapper();

	@Override
	public String convertToDatabaseColumn(NotificationData attribute) {
		if (attribute == null) {
			return null;
		}

		try {
			return objectMapper.writeValueAsString(attribute);
		} catch (JsonProcessingException e) {
			log.error("NotificationData JSON 변환 실패: {}", e.getMessage());
			throw new RuntimeException("NotificationData를 JSON으로 변환하는데 실패했습니다.", e);
		}
	}

	@Override
	public NotificationData convertToEntityAttribute(String dbData) {
		if (dbData == null || dbData.isEmpty()) {
			return null;
		}

		try {
			return objectMapper.readValue(dbData, NotificationData.class);
		} catch (IOException e) {
			log.error("JSON NotificationData 변환 실패: {}", e.getMessage());
			throw new RuntimeException("JSON을 NotificationData로 변환하는데 실패했습니다.", e);
		}
	}
}
