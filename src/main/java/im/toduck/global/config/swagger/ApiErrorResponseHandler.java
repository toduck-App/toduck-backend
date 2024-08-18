package im.toduck.global.config.swagger;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import im.toduck.global.annotation.swagger.ApiErrorResponseExplanation;
import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import im.toduck.global.exception.ExceptionCode;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import lombok.Builder;
import lombok.Getter;

/**
 * {@code ApiErrorResponseHandler} 클래스는 애플리케이션 내의 예외 상황에 대한
 * 공통 응답을 Swagger API 문서에 반영합니다.
 *
 * <p>이 클래스는 컨트롤러 메서드에 정의된 복수의 {@link ApiErrorResponseExplanation} 어노테이션을 분석하여,
 * 예외 코드에 대응하는 HTTP 응답 예제를 Swagger 문서에 추가합니다.</p>
 *
 * @see ApiErrorResponseExplanation
 */
@Component
public class ApiErrorResponseHandler {

	public void handleApiErrorResponse(Operation operation, HandlerMethod handlerMethod) {
		ApiResponseExplanations apiResponseExplanations
			= handlerMethod.getMethodAnnotation(ApiResponseExplanations.class);

		if (apiResponseExplanations != null) {
			generateResponseCodeResponseExample(operation, Arrays.asList(apiResponseExplanations.errors()));
		}
	}

	private void generateResponseCodeResponseExample(Operation operation,
		List<ApiErrorResponseExplanation> apiErrorResponseExamples) {
		ApiResponses responses = operation.getResponses();

		Map<Integer, List<ExampleHolder>> statusWithExampleHolders = apiErrorResponseExamples.stream()
			.map(this::createExampleHolder)
			.collect(Collectors.groupingBy(ExampleHolder::getHttpStatusCode));

		addExamplesToResponses(responses, statusWithExampleHolders);
	}

	private ExampleHolder createExampleHolder(ApiErrorResponseExplanation apiErrorResponseExample) {
		ExceptionCode exceptionCode = apiErrorResponseExample.exceptionCode();
		return ExampleHolder.builder()
			.httpStatusCode(exceptionCode.getHttpStatus().value())
			.name(exceptionCode.name())
			.errorCode(exceptionCode.getErrorCode())
			.description(exceptionCode.getDescription())
			.holder(createSwaggerExample(exceptionCode, exceptionCode.getDescription()))
			.build();
	}

	private Example createSwaggerExample(ExceptionCode exceptionCode, String description) {
		im.toduck.global.presentation.ApiResponse<Object> apiResponse
			= im.toduck.global.presentation.ApiResponse.createError(exceptionCode);

		Example example = new Example();
		example.setValue(apiResponse);
		example.setDescription(description); // 설명을 예제에 추가

		return example;
	}

	private void addExamplesToResponses(
		ApiResponses responses,
		Map<Integer, List<ExampleHolder>> statusWithExampleHolders
	) {
		statusWithExampleHolders.forEach((status, exampleHolders) -> {
			Content content = new Content();
			MediaType mediaType = new MediaType();
			ApiResponse apiResponse = new ApiResponse();

			exampleHolders.forEach(
				exampleHolder -> mediaType.addExamples(exampleHolder.getName(), exampleHolder.getHolder())
			);

			content.addMediaType("application/json", mediaType);
			apiResponse.setContent(content);
			responses.addApiResponse(String.valueOf(status), apiResponse);
		});
	}

	@Getter
	@Builder
	public static class ExampleHolder {
		private final int httpStatusCode;
		private final String name;
		private final int errorCode;
		private final String description;
		private final Example holder;
	}
}
