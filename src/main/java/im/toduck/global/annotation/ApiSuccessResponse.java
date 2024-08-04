package im.toduck.global.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.http.HttpStatus;

import im.toduck.domain.swaggertest.presentation.dto.response.CreateTeamspaceResponse;

/**
 * {@code ApiSuccessResponse} 어노테이션은 특정 API 엔드포인트에서 성공적으로 처리된
 * 요청에 대한 응답 형식을 정의하는 데 사용됩니다.
 *
 * 이 어노테이션을 사용하여 성공 응답의 HTTP 상태 코드와 응답 본문에 대한 스키마를 명시할 수 있습니다.
 * 지정된 {@link HttpStatus}와 {@code responseClass}는 Swagger 문서에 반영되어 해당 API에서
 * 성공적으로 처리된 요청에 대한 응답 형식을 API 소비자에게 명확히 전달합니다.
 *
 * <p>이 어노테이션은 메서드 레벨에서 사용되며, 지정된 HTTP 상태 코드와 응답 클래스는
 * 해당 메서드의 성공 응답 형식을 Swagger 문서에 정의합니다.</p>
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
 * @see HttpStatus
 * @see CreateTeamspaceResponse
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiSuccessResponse {
	HttpStatus status() default HttpStatus.OK;

	Class<?> responseClass();
}
