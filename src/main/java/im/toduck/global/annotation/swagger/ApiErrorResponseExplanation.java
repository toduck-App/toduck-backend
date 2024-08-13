package im.toduck.global.annotation.swagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import im.toduck.global.exception.ExceptionCode;

/**
 * {@code ApiErrorResponseExplanation} 어노테이션은 특정 API 엔드포인트에서 발생할 수 있는
 * 예외 상황에 대한 응답 예제를 정의하는 데 사용됩니다.
 *
 * <p>이 어노테이션은 API 문서에서 발생할 수 있는 오류에 대한 설명과 예제 응답을 제공합니다.
 * 이를 통해 Swagger 문서에 API의 오류 응답 형식을 명확히 전달할 수 있습니다.</p>
 *
 * @see ApiResponseExplanations
 * @see ExceptionCode
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiErrorResponseExplanation {
	ExceptionCode exceptionCode();
}
