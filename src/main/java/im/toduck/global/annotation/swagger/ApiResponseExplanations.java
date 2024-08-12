package im.toduck.global.annotation.swagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@code ApiResponseExplanations} 어노테이션은 API 엔드포인트에서의 성공 및 오류 응답에 대한
 * 설명을 정의합니다.
 *
 * <p>이 어노테이션을 통해 API 엔드포인트의 성공 응답과 오류 응답을 Swagger 문서에 명확히 정의할 수 있습니다.</p>
 *
 * <p>예제 사용법:</p>
 * <pre>
 * {@literal @}ApiResponseExplanations(
 *     success = @ApiSuccessResponseExplanation(
 *         description = "사용자 정보가 성공적으로 업데이트되었습니다.",
 *         status = HttpStatus.OK,
 *         responseClass = UpdateUserResponse.class
 *     ),
 *     errors = {
 *         &#064;ApiErrorResponseExplanation(
 *             description = "잘못된 사용자 ID입니다.",
 *             exceptionCode = ExceptionCode.INVALID_USER_ID
 *         ),
 *         &#064;ApiErrorResponseExplanation(
 *             description = "입력된 데이터가 유효하지 않습니다.",
 *             exceptionCode = ExceptionCode.INVALID_INPUT
 *         )
 *     }
 * )
 * public ResponseEntity{@literal <}UpdateUserResponse> updateUserInfo(
 *     &#064;RequestBody  @Valid final UpdateUserRequest request
 * );
 * </pre>
 *
 * @see ApiSuccessResponseExplanation
 * @see ApiErrorResponseExplanation
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiResponseExplanations {

	ApiSuccessResponseExplanation success() default @ApiSuccessResponseExplanation();

	ApiErrorResponseExplanation[] errors() default {};
}
