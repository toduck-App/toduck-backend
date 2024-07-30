package im.toduck.global.presentation.builder;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.exception.VoException;
import im.toduck.global.presentation.ApiResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * API 응답을 포함하는 ResponseEntity 생성 클래스.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponseEntityBuilder {

	public static ResponseEntity<Object> createValidationErrorEntity(Map<String, String> errors) {
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(ApiResponse.createValidationError(errors));
	}

	public static <T> ResponseEntity<ApiResponse<T>> createErrorResponseEntityFromException(CommonException ex) {
		return ResponseEntity
			.status(ex.getHttpStatus())
			.body(ApiResponse.createErrorFromException(ex));
	}

	public static ResponseEntity<Object> createErrorResponseEntity(ExceptionCode ec) {
		return ResponseEntity
			.status(ec.getHttpStatus())
			.body(ApiResponse.createError(ec));
	}

	public static ResponseEntity<ApiResponse<String>> createVoErrorEntity(VoException ex) {
		return ResponseEntity
			.status(HttpStatus.UNPROCESSABLE_ENTITY)
			.body(ApiResponse.createVoError(ex));
	}

	public static ResponseEntity<Object> createServerErrorEntity() {
		return ResponseEntity
			.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(ApiResponse.createServerError());
	}
}
