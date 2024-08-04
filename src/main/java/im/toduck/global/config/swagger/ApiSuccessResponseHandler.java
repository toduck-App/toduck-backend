package im.toduck.global.config.swagger;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import im.toduck.global.annotation.ApiSuccessResponse;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

/**
 * {@code ApiSuccessResponseHandler} 클래스는 API 엔드포인트의 성공 응답을 처리하고,
 * Swagger 문서에 정의된 성공 응답 형식을 업데이트하는 역할을 합니다.
 *
 * 이 클래스는 {@link ApiSuccessResponse} 어노테이션이 적용된 메서드를 찾아
 * 성공 응답의 HTTP 상태 코드와 응답 본문 스키마를 Swagger 문서에 추가합니다.
 *
 * <p>기본적으로 HTTP 상태 코드 200(OK)에 대한 응답을 제거하고, 어노테이션에
 * 지정된 상태 코드와 응답 본문 스키마를 포함한 성공 응답을 추가합니다.</p>
 *
 * <p>예제 사용법:</p>
 * <pre>
 * {@literal @}ApiSuccessResponse(
 *     status = HttpStatus.CREATED,
 *     responseClass = CreateTeamspaceResponse.class
 * )
 * ResponseEntity{@literal <}CreateTeamspaceResponse> createTeamspace(...);
 * </pre>
 *
 * @see ApiSuccessResponse
 */
@Component
public class ApiSuccessResponseHandler {

	private static final String APPLICATION_JSON = "application/json";
	private static final String RESPONSE_DESCRIPTION = "성공 응답";
	private static final int SUCCESS_CODE = 20000;
	private static final String DEFAULT_NULL_MESSAGE = "null";

	public void handleApiSuccessResponse(Operation operation, HandlerMethod handlerMethod) {
		ApiSuccessResponse apiSuccessResponse = handlerMethod.getMethodAnnotation(ApiSuccessResponse.class);

		if (apiSuccessResponse != null) {
			ApiResponses responses = operation.getResponses();
			// 기본 200 OK 응답이 존재하면 제거
			responses.remove("200");

			Schema<?> responseSchema = new Schema<>()
				.description(RESPONSE_DESCRIPTION)
				.addProperty("code", new Schema<>().type("integer").example(SUCCESS_CODE))
				.addProperty("content",
					new Schema<>().$ref("#/components/schemas/" + apiSuccessResponse.responseClass().getSimpleName())
				)
				.addProperty("message", new Schema<>().example(DEFAULT_NULL_MESSAGE));

			ApiResponse apiResponse = new ApiResponse()
				.content(
					new Content()
						.addMediaType(
							APPLICATION_JSON,
							new MediaType().schema(responseSchema)
						)
				);
			responses.addApiResponse(String.valueOf(apiSuccessResponse.status().value()), apiResponse);
		}
	}
}
