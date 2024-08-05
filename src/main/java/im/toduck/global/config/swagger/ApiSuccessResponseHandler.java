package im.toduck.global.config.swagger;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.annotation.swagger.ApiSuccessResponseExplanation;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

/**
 * {@code ApiSuccessResponseHandler} 클래스는 API 엔드포인트의 성공 응답을 처리하고,
 * Swagger 문서에 정의된 성공 응답 형식을 업데이트합니다.
 *
 * <p>이 클래스는 {@link ApiSuccessResponseExplanation} 어노테이션이 적용된 메서드를 찾아
 * 성공 응답의 HTTP 상태 코드와 응답 본문 스키마를 Swagger 문서에 추가합니다.</p>
 *
 * <p>기본 응답 예시를 제거하고, 어노테이션에
 * 지정된 상태 코드와 응답 본문 스키마를 포함한 성공 응답을 추가합니다.</p>
 *
 * @see ApiSuccessResponseExplanation
 */
@Component
public class ApiSuccessResponseHandler {

	private static final String APPLICATION_JSON = "application/json";
	private static final int SUCCESS_CODE = 20000;
	private static final String DEFAULT_NULL_MESSAGE = "null";

	public void handleApiSuccessResponse(Operation operation, HandlerMethod handlerMethod) {
		ApiResponseExplanations apiResponseExplanations
			= handlerMethod.getMethodAnnotation(ApiResponseExplanations.class);

		if (apiResponseExplanations == null) {
			return;
		}

		ApiSuccessResponseExplanation apiSuccessResponseExplanation = apiResponseExplanations.success();

		if (apiSuccessResponseExplanation != null) {
			ApiResponses responses = operation.getResponses();
			// 기본 200 OK 응답이 존재하면 제거
			responses.remove("200");

			io.swagger.v3.oas.models.media.Schema<?> responseSchema = new Schema<>()
				.addProperty("code",
					new Schema<>().type("integer").example(SUCCESS_CODE))
				.addProperty("content",
					apiSuccessResponseExplanation.responseClass()
						.isAssignableFrom(ApiSuccessResponseExplanation.EmptyClass.class)
						?
						new Schema<>().type("object").example(Map.of())
						:
						new Schema<>().$ref(
							"#/components/schemas/" + apiSuccessResponseExplanation.responseClass().getSimpleName())
				)
				.addProperty("message", new io.swagger.v3.oas.models.media.Schema<>().example(DEFAULT_NULL_MESSAGE));

			ApiResponse apiResponse = new ApiResponse()
				.description(apiSuccessResponseExplanation.description())
				.content(
					new Content()
						.addMediaType(
							APPLICATION_JSON,
							new MediaType().schema(responseSchema)
						)
				);
			responses.addApiResponse(String.valueOf(apiSuccessResponseExplanation.status().value()), apiResponse);
		}
	}
}
