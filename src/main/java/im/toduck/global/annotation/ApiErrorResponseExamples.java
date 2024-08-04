package im.toduck.global.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import im.toduck.global.exception.ExceptionCode;

/**
 * {@code ApiErrorResponseExamples} 어노테이션은 특정 API 메서드에서 발생할 수 있는
 * 복수의 예외에 대한 응답 예제를 정의합니다.
 *
 * <p>단일 예외 응답을 정의하려면 {@link ApiErrorResponseExample}를 사용하세요.</p>
 * <p>보다 자세한 설명은 {@link ApiErrorResponseExample}를 확인하세요.</p>
 *
 * <p>예제 사용법:</p>
 * <pre>
 * {@literal @}ApiErrorResponseExamples({
 *     ExceptionCode.INVALID_EMAIL_OR_PASSWORD,
 *     ExceptionCode.USER_NOT_FOUND
 * })
 * ResponseEntity{@literal <}UserResponse> getUser(...);
 * </pre>
 *
 * @see ApiErrorResponseExample
 * @see ExceptionCode
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiErrorResponseExamples {
	ExceptionCode[] value();
}
