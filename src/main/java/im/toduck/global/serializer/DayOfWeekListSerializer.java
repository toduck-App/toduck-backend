package im.toduck.global.serializer;

import java.io.IOException;
import java.time.DayOfWeek;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class DayOfWeekListSerializer extends JsonSerializer<List<DayOfWeek>> {

	@Override
	public void serialize(List<DayOfWeek> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		if (value == null || value.isEmpty()) {
			gen.writeStartArray();
			gen.writeEndArray();
			return;
		}

		gen.writeStartArray();
		for (DayOfWeek day : value) {
			gen.writeString(day.name());
		}
		gen.writeEndArray();
	}
}
