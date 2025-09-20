package im.toduck.global.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.exception.VoException;
import im.toduck.global.presentation.ApiResponse;
import im.toduck.global.presentation.builder.ApiResponseEntityBuilder;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * GlobalExceptionHandler 클래스는 애플리케이션 전역에서 발생하는 다양한 예외를 처리합니다.
 * ResponseEntityExceptionHandler를 상속하여 특정 상황에 대한 사용자 정의 예외 처리를 제공합니다.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	private static final Map<Class<? extends JwtException>, ExceptionCode> JWT_EXCEPTION_CODE_MAP = Map.of(
		ExpiredJwtException.class, ExceptionCode.EXPIRED_ACCESS_TOKEN,
		MalformedJwtException.class, ExceptionCode.MALFORMED_TOKEN,
		SignatureException.class, ExceptionCode.TAMPERED_TOKEN,
		UnsupportedJwtException.class, ExceptionCode.UNSUPPORTED_JWT_TOKEN
	);

	/**
	 * CommonException을 처리합니다.
	 *
	 * @param ex 발생한 CommonException
	 * @return ApiResponse를 포함하는 ResponseEntity
	 */
	@ExceptionHandler(CommonException.class)
	public ResponseEntity<ApiResponse<CommonException>> handleCommonException(CommonException ex) {
		return ApiResponseEntityBuilder.createErrorResponseEntityFromException(ex);
	}

	/**
	 * JwtException 처리합니다.
	 *
	 * @param ex 발생한 JwtException
	 * @return ApiResponse를 포함하는 ResponseEntity
	 */
	@ExceptionHandler(JwtException.class)
	public ResponseEntity<Object> handleJwtException(JwtException ex) {
		Class<? extends Exception> exceptionClass = ex.getClass();
		ExceptionCode exceptionCode = JWT_EXCEPTION_CODE_MAP.get(exceptionClass);
		return ApiResponseEntityBuilder.createErrorResponseEntity(exceptionCode);
	}

	/**
	 * VoException을 처리합니다.
	 *
	 * @param ex 발생한 VoException
	 * @return ApiResponse를 포함하는 ResponseEntity
	 */
	@ExceptionHandler(VoException.class)
	public ResponseEntity<ApiResponse<String>> handleVoErrorExceptions(VoException ex) {
		return ApiResponseEntityBuilder.createVoErrorEntity(ex);
	}

	/**
	 * 권한 부족으로 접근이 거부될 때 발생하는 AuthorizationDeniedException을 처리합니다.
	 */
	@ExceptionHandler(AuthorizationDeniedException.class)
	public ResponseEntity<Object> handleAuthorizationDenied(AuthorizationDeniedException ex) {
		return ApiResponseEntityBuilder.createErrorResponseEntity(ExceptionCode.FORBIDDEN_ACCESS_TOKEN);
	}

	/**
	 * 리소스를 찾을 수 없을 때 발생하는 NoResourceFoundException을 처리합니다.
	 */
	@Override
	@Nullable
	protected ResponseEntity<Object> handleNoResourceFoundException(
		NoResourceFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request
	) {
		return ApiResponseEntityBuilder.createErrorResponseEntity(ExceptionCode.NOT_FOUND_RESOURCE);
	}

	/**
	 * 메서드 인자 유효성 검사 실패 시 발생하는 MethodArgumentNotValidException을 처리합니다.
	 */
	@Override
	@Nullable
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
		MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request
	) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach(error -> {
			if (error instanceof FieldError) {
				String fieldName = ((FieldError)error).getField();
				String errorMessage = error.getDefaultMessage();
				errors.put(fieldName, errorMessage);
			} else {
				String objectName = error.getObjectName();
				String errorMessage = error.getDefaultMessage();
				errors.put(objectName, errorMessage);
			}
		});

		return ApiResponseEntityBuilder.createValidationErrorEntity(errors);
	}

	/**
	 * 메서드 인자 유효성 검사 실패 시 발생하는 HandlerMethodValidationException을 처리합니다.
	 */
	@Override
	@Nullable
	protected ResponseEntity<Object> handleHandlerMethodValidationException(
		HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request
	) {
		Map<String, String> errors = new HashMap<>();

		ex.getAllValidationResults().forEach(result -> {
			MethodParameter param = result.getMethodParameter();
			String fieldName = param.getParameterName();

			String errorMessage = result.getResolvableErrors().stream()
				.map(MessageSourceResolvable::getDefaultMessage)
				.findFirst()
				.orElse("유효성 검사 오류가 발생했습니다.");

			errors.put(fieldName, errorMessage);
		});

		return ApiResponseEntityBuilder.createValidationErrorEntity(errors);
	}

	/**
	 * 필수 요청 파라미터가 누락되었을 때 발생하는 MissingServletRequestParameterException을 처리합니다.
	 */
	@Override
	@Nullable
	protected ResponseEntity<Object> handleMissingServletRequestParameter(
		MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request
	) {
		String missingParam = ex.getParameterName();
		Map<String, String> errors = new HashMap<>();
		errors.put(missingParam, String.format("필수 쿼리 파라미터 '%s'가 누락되었습니다.", missingParam));
		return ApiResponseEntityBuilder.createValidationErrorEntity(errors);
	}

	/**
	 * 지원되지 않는 HTTP 메서드로 요청할 때 발생하는 HttpRequestMethodNotSupportedException을 처리합니다.
	 */
	@Override
	@Nullable
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
		HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request
	) {
		return ApiResponseEntityBuilder.createErrorResponseEntity(ExceptionCode.METHOD_FORBIDDEN);
	}

	/**
	 * 내부 서버 오류를 처리합니다.
	 */
	@Override
	@Nullable
	protected ResponseEntity<Object> handleExceptionInternal(
		Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request
	) {
		if (request instanceof ServletWebRequest servletWebRequest) {
			HttpServletResponse response = servletWebRequest.getResponse();
			if (response != null && response.isCommitted()) {
				if (logger.isWarnEnabled()) {
					logger.warn("Response already committed. Ignoring: " + ex);
				}
				return null;
			}
		}
		logger.error("Unexpected error 발생: " + ex.getMessage(), ex);
		return ApiResponseEntityBuilder.createServerErrorEntity();
	}

	/**
	 * 읽을 수 없는 HTTP 메시지가 들어왔을 때 발생하는 HttpMessageNotReadableException을 처리합니다.
	 */
	@Override
	@Nullable
	protected ResponseEntity<Object> handleHttpMessageNotReadable(
		HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request
	) {
		Map<String, String> errors = new HashMap<>();
		if (ex.getCause() instanceof MismatchedInputException mismatchedInputException) {
			StringBuilder sb = new StringBuilder();

			for (JsonMappingException.Reference reference : mismatchedInputException.getPath()) {
				String fieldName = reference.getFieldName();
				if (fieldName == null) {
					sb.append("[").append(reference.getIndex()).append("]");
				} else {
					if (!sb.isEmpty()) {
						sb.append(".");
					}
					sb.append(reference.getFieldName());
				}
			}

			errors.put(sb.toString(), "필드의 값이 잘못되었습니다. Type을 확인하세요.");
		} else {
			errors.put("common", "확인할 수 없는 형태의 데이터가 들어왔습니다. JSON 형식인지 확인하세요.");
			log.error("HttpMessageNotReadable 에러 발생: {}", ex.getMessage(), ex);
		}
		return ApiResponseEntityBuilder.createValidationErrorEntity(errors);
	}

	/**
	 * 메서드 인자 타입이 맞지 않을 때 발생하는 MethodArgumentTypeMismatchException을 처리합니다.
	 *
	 * @param ex 발생한 MethodArgumentTypeMismatchException
	 * @return 유효성 검사 오류 메시지를 포함하는 ResponseEntity
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
		Map<String, String> errors = new HashMap<>();
		String fieldName = ex.getName();
		String errorMessage = String.format("값 '%s'이(가) 유효하지 않습니다. 필요한 타입: '%s'", ex.getValue(),
			ex.getRequiredType() == null ? "알수없음" : ex.getRequiredType().getSimpleName());
		errors.put(fieldName, errorMessage);

		return ApiResponseEntityBuilder.createValidationErrorEntity(errors);
	}

	/**
	 * 예상치 못한 일반적인 예외를 처리합니다.
	 *
	 * @param ex 발생한 Exception
	 * @return 서버 오류 메시지를 포함하는 ResponseEntity
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleUnexpectedException(Exception ex) {
		logger.error("Unexpected error 발생: " + ex.getMessage(), ex);
		return ApiResponseEntityBuilder.createServerErrorEntity();
	}
}
