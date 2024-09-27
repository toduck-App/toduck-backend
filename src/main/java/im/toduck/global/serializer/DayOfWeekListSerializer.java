package im.toduck.global.serializer;

import java.io.IOException;
import java.time.DayOfWeek;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import jakarta.validation.constraints.NotNull;

public class DayOfWeekListSerializer extends JsonSerializer<List<DayOfWeek>> {

	@Override
	public void serialize(@NotNull List<DayOfWeek> value, JsonGenerator gen, SerializerProvider provider)
		throws IOException {
		if (value == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}

		gen.writeStartArray();
		for (DayOfWeek day : value) {
			gen.writeString(day.name());
		}
		gen.writeEndArray();
	}
}
