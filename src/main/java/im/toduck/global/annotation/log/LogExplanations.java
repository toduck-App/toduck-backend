package im.toduck.global.annotation.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import im.toduck.global.aop.log.ProdLogAspect;

/**
 * {@code LogExplanations}는 메서드에 대한 일반 로그 설명 및 예외 로그 설명을 정의하기 위한 커스텀 어노테이션입니다.
 * <p>
 * 이 어노테이션은 AOP 기반 로깅 처리에서 로그 설명과 예외 상황에 맞는 로그 레벨을 동적으로 결정하기 위해 사용됩니다.
 * {@link ProdLogAspect} 클래스에서 이 어노테이션을 참조하여, 메서드 실행 시 적절한 로그 메시지와 로그 레벨을 출력합니다.
 * </p>
 * <h2>사용 목적</h2>
 * <ul>
 *     <li>일반적인 메서드 호출에 대한 로그 설명을 제공하기 위해 {@code general} 속성을 사용합니다.</li>
 *     <li>예외 발생 시 해당 예외에 대한 로그 설명과 로그 레벨을 지정하기 위해 {@code exception} 속성을 사용합니다.</li>
 * </ul>
 * <h2>구성 요소</h2>
 * <ul>
 *     <li>{@code general}: 메서드 실행에 대한 일반 로그 설명을 정의하는 {@link GeneralLogExplanation} 배열입니다.</li>
 *     <li>{@code exception}: 예외 상황에 대한 로그 설명과 로그 레벨을 정의하는 {@link ExceptionLogExplanation} 배열입니다.</li>
 * </ul>
 * <h2>적용 대상</h2>
 * <p>
 * 이 어노테이션은 메서드 수준에서만 적용되며, {@link ProdLogAspect}와 함께 사용하여 메서드 호출 시 발생하는 로그를 보다 상세하게 관리할 수 있습니다.
 * </p>
 * <pre>
 * 예시:
 * {@code
 * @LogExplanations(
 *   general = {@GeneralLogExplanation(description = "유저 정보 조회")},
 *   exception = {
 *     @ExceptionLogExplanation(code = ErrorCode.USER_NOT_FOUND, description = "유저를 찾을 수 없음", level = LogLevel.WARN)
 *   }
 * )
 * public User getUser(String userId) {
 * }
 * }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogExplanations {
	GeneralLogExplanation[] general() default {};

	ExceptionLogExplanation[] exception() default {};
}
