package im.toduck.global.config.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * 문자열에서 공백을 제거하는 커스텀 JSON 역직렬화기입니다.
 * 입력된 문자열의 앞뒤 공백을 제거하고, 빈 문자열인 경우 null을 반환합니다.
 */
public class StringStripJsonDeserializer extends JsonDeserializer<String> {

	/**
	 * JSON 문자열 값을 역직렬화합니다.
	 * 문자열의 앞뒤 공백을 제거하고, 결과가 빈 문자열이면 null을 반환합니다.
	 *
	 * @param jp JSON 파서 객체
	 * @param ctx 역직렬화 컨텍스트
	 * @return 공백이 제거된 문자열 또는 null
	 * @throws IOException JSON 파싱 중 발생할 수 있는 입출력 예외
	 */
	@Override
	public String deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
		String value = jp.getValueAsString();

		if (value == null) {
			return null;
		}

		String valueStripped = value.strip();

		return !valueStripped.isEmpty() ? valueStripped : null;
	}
}
