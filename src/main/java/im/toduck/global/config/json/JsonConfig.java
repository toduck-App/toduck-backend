package im.toduck.global.config.json;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Configuration
public class JsonConfig {

	/**
	 * ObjectMapper 빈을 생성합니다.
	 * Jackson2ObjectMapperBuilder를 사용하여 커스텀 모듈을 추가합니다.
	 *
	 * @return ObjectMapper 객체
	 */
	@Bean
	public ObjectMapper objectMapper() {
		return Jackson2ObjectMapperBuilder
			.json()
			.modules(customJsonDeserializeModule())
			.build();
	}

	/**
	 * 커스텀 JSON 역직렬화 모듈을 설정합니다.
	 *
	 * @return SimpleModule 객체
	 */
	private SimpleModule customJsonDeserializeModule() {
		SimpleModule module = new SimpleModule();
		module.addDeserializer(String.class, new StringStripJsonDeserializer());

		return module;
	}
}
