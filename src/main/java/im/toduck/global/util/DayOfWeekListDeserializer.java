package im.toduck.global.util;

import java.io.IOException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

public class DayOfWeekListDeserializer extends JsonDeserializer<List<DayOfWeek>> {

	private static final String ERROR_NOT_ARRAY = "요일 목록은 배열 형태여야 합니다.";
	private static final String ERROR_NOT_STRING = "요일은 문자열이어야 합니다.";
	private static final String ERROR_EMPTY_DAY = "요일은 비어있을 수 없습니다.";
	private static final String ERROR_INVALID_DAY = "유효하지 않은 요일입니다: ";
	private static final String ERROR_EMPTY_LIST = "요일 목록은 비어있을 수 없습니다.";

	@Override
	public List<DayOfWeek> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
		List<DayOfWeek> daysOfWeek = new ArrayList<>();
		JsonNode node = jp.getCodec().readTree(jp);

		if (!node.isArray()) {
			throw MismatchedInputException.from(jp, List.class, ERROR_NOT_ARRAY);
		}

		for (int i = 0; i < node.size(); i++) {
			JsonNode dayNode = node.get(i);
			if (!dayNode.isTextual()) {
				throw InvalidFormatException.from(jp, ERROR_NOT_STRING, dayNode, DayOfWeek.class);
			}

			String dayString = dayNode.asText().trim().toUpperCase();
			if (dayString.isEmpty()) {
				throw InvalidFormatException.from(jp, ERROR_EMPTY_DAY, dayString, DayOfWeek.class);
			}

			try {
				daysOfWeek.add(DayOfWeek.valueOf(dayString));
			} catch (IllegalArgumentException e) {
				throw InvalidFormatException.from(jp, ERROR_INVALID_DAY + dayString, dayString, DayOfWeek.class);
			}
		}

		if (daysOfWeek.isEmpty()) {
			throw MismatchedInputException.from(jp, List.class, ERROR_EMPTY_LIST);
		}

		return daysOfWeek;
	}
}
