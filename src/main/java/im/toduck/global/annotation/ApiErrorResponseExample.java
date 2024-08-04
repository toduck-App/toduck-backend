package im.toduck.global.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import im.toduck.global.exception.ExceptionCode;

/**
 * {@code ApiErrorResponseExample} 어노테이션은 특정 API 엔드포인트에서 발생할 수 있는
 * 단일 예외 상황에 대한 응답 예제를 정의하는 데 사용됩니다.
 *
 * 이 어노테이션을 사용하여 하나의 {@link ExceptionCode}를 지정할 수 있으며,
 * 지정된 예외 코드는 Swagger 문서에 반영되어 해당 API에서 발생 가능한
 * 에러 상황을 API 소비자에게 공통응답형식에 맞게 명확히 전달합니다.
 *
 * <p>이 어노테이션은 메서드 레벨에서 사용되며, 지정된 예외 코드를 기반으로
 * 해당 메서드의 에러 응답 형식을 Swagger 문서에 정의합니다.</p>
 *
 * <p>예제 사용법:</p>
 * <pre>
 * {@literal @}ApiErrorResponseExample(ExceptionCode.INVALID_EMAIL_OR_PASSWORD)
 * ResponseEntity{@literal <}UserResponse> loginUser(...);
 * </pre>
 *
 * @see ApiErrorResponseExamples
 * @see ExceptionCode
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiErrorResponseExample {
	ExceptionCode value();
}
