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
	@Override
	public List<DayOfWeek> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
		List<DayOfWeek> daysOfWeek = new ArrayList<>();
		JsonNode node = jp.getCodec().readTree(jp);

		if (!node.isArray()) {
			throw MismatchedInputException.from(jp, List.class, "");
		}

		for (int i = 0; i < node.size(); i++) {
			JsonNode dayNode = node.get(i);
			if (!dayNode.isTextual()) {
				throw InvalidFormatException.from(jp, "", dayNode, DayOfWeek.class);
			}

			String dayString = dayNode.asText().toUpperCase();
			try {
				daysOfWeek.add(DayOfWeek.valueOf(dayString));
			} catch (IllegalArgumentException e) {
				throw InvalidFormatException.from(jp, "", "", DayOfWeek.class);
			}
		}

		if (daysOfWeek.isEmpty()) {
			throw MismatchedInputException.from(jp, List.class, "");
		}

		return daysOfWeek;
	}
}
