package im.toduck.global.config.swagger;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import im.toduck.global.annotation.ApiErrorResponseExample;
import im.toduck.global.annotation.ApiErrorResponseExamples;
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
 * 공통 응답을 Swagger API 문서에 반영하는 역할을 수행합니다.
 *
 * 이 클래스는 컨트롤러 메서드에 정의된 {@link ApiErrorResponseExamples} 또는
 * {@link ApiErrorResponseExample} 어노테이션을 분석하여, 예외 코드에 대응하는
 * HTTP 응답 예제를 Swagger 문서에 추가합니다.
 *
 */
@Component
public class ApiErrorResponseHandler {
	public void handleApiErrorResponse(Operation operation, HandlerMethod handlerMethod) {
		ApiErrorResponseExamples apiErrorResponseExamples = handlerMethod.getMethodAnnotation(
			ApiErrorResponseExamples.class);
		if (apiErrorResponseExamples != null) {
			generateResponseCodeResponseExample(operation, Arrays.asList(apiErrorResponseExamples.value()));
		} else {
			ApiErrorResponseExample apiErrorResponseExample = handlerMethod.getMethodAnnotation(
				ApiErrorResponseExample.class);
			if (apiErrorResponseExample != null) {
				generateResponseCodeResponseExample(operation, List.of(apiErrorResponseExample.value()));
			}
		}
	}

	private void generateResponseCodeResponseExample(Operation operation, List<ExceptionCode> exceptionCodes) {
		ApiResponses responses = operation.getResponses();

		Map<Integer, List<ExampleHolder>> statusWithExampleHolders = exceptionCodes.stream()
			.map(this::createExampleHolder)
			.collect(Collectors.groupingBy(ExampleHolder::getHttpStatusCode));

		addExamplesToResponses(responses, statusWithExampleHolders);
	}

	private ExampleHolder createExampleHolder(ExceptionCode exceptionCode) {
		return ExampleHolder.builder()
			.httpStatusCode(exceptionCode.getHttpStatus().value())
			.name(exceptionCode.name())
			.errorCode(exceptionCode.getErrorCode())
			.holder(createSwaggerExample(exceptionCode))
			.build();
	}

	private Example createSwaggerExample(ExceptionCode exceptionCode) {
		im.toduck.global.presentation.ApiResponse<Object> apiResponse
			= im.toduck.global.presentation.ApiResponse.createError(exceptionCode);

		Example example = new Example();
		example.setValue(apiResponse);
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
		private final Example holder;
	}
}
