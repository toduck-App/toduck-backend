package im.toduck.global.util;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.time.DayOfWeek;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import im.toduck.global.serializer.DayOfWeekListDeserializer;

class DayOfWeekListDeserializerTest {

	private ObjectMapper objectMapper;
	private DayOfWeekListDeserializer deserializer;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
		deserializer = new DayOfWeekListDeserializer();
	}

	@Nested
	class DeserializeTest {

		@Test
		void 올바른_요일_목록을_역직렬화할_수_있다() throws IOException {
			// given
			String json = "[\"MONDAY\", \"WEDNESDAY\", \"FRIDAY\"]";

			// when
			List<DayOfWeek> result = deserialize(json);

			// then
			assertThat(result).containsExactly(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY);
		}

		@Test
		void 소문자_요일도_역직렬화할_수_있다() throws IOException {
			// given
			String json = "[\"monday\", \"wednesday\", \"friday\"]";

			// when
			List<DayOfWeek> result = deserialize(json);

			// then
			assertThat(result).containsExactly(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY);
		}

		@Test
		void 배열이_아닌_입력은_예외를_발생시킨다() {
			// given
			String json = "\"MONDAY\"";

			// when & then
			assertThatThrownBy(() -> deserialize(json))
				.isInstanceOf(MismatchedInputException.class);
		}

		@Test
		void 잘못된_요일_문자열은_예외를_발생시킨다() {
			// given
			String json = "[\"MONDAY\", \"INVALID_DAY\"]";

			// when & then
			assertThatThrownBy(() -> deserialize(json))
				.isInstanceOf(InvalidFormatException.class);
		}

		@Test
		void 숫자_입력은_예외를_발생시킨다() {
			// given
			String json = "[\"MONDAY\", 2]";

			// when & then
			assertThatThrownBy(() -> deserialize(json))
				.isInstanceOf(InvalidFormatException.class);
		}

		@Test
		void 빈_배열은_예외를_발생시킨다() {
			// given
			String json = "[]";

			// when & then
			assertThatThrownBy(() -> deserialize(json))
				.isInstanceOf(MismatchedInputException.class);
		}

		@Test
		void null_입력은_예외를_발생시킨다() {
			// given
			String json = "null";

			// when & then
			assertThatThrownBy(() -> deserialize(json))
				.isInstanceOf(MismatchedInputException.class);
		}

		@Test
		void 중복된_요일은_허용된다() throws IOException {
			// given
			String json = "[\"MONDAY\", \"MONDAY\", \"WEDNESDAY\"]";

			// when
			List<DayOfWeek> result = deserialize(json);

			// then
			assertThat(result).containsExactly(DayOfWeek.MONDAY, DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY);
		}

		@Test
		void 공백이_포함된_요일은_허용된다() throws IOException {
			// given
			String json = "[\"MONDAY \", \"WEDNESDAY\"]";

			// when
			List<DayOfWeek> result = deserialize(json);

			// then
			assertThat(result).containsExactly(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY);
		}

		@Test
		void 빈_문자열_요일은_예외를_발생시킨다() {
			// given
			String json = "[\"MONDAY\", \"\", \"WEDNESDAY\"]";

			// when & then
			assertThatThrownBy(() -> deserialize(json))
				.isInstanceOf(InvalidFormatException.class);
		}

		@Test
		void 모든_요일을_포함하는_입력을_처리할_수_있다() throws IOException {
			// given
			String json = "[\"MONDAY\", \"TUESDAY\", \"WEDNESDAY\", \"THURSDAY\", \"FRIDAY\", \"SATURDAY\", \"SUNDAY\"]";

			// when
			List<DayOfWeek> result = deserialize(json);

			// then
			assertThat(result).containsExactly(
				DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
				DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY
			);
		}
	}

	private List<DayOfWeek> deserialize(String json) throws IOException {
		JsonParser jsonParser = objectMapper.getFactory().createParser(json);
		return deserializer.deserialize(jsonParser, objectMapper.getDeserializationContext());
	}
}
