package im.toduck.global.aop.log;

import java.util.Optional;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import im.toduck.global.annotation.log.GeneralLogExplanation;
import im.toduck.global.annotation.log.LogExplanations;
import im.toduck.global.log.properties.InfoLogProperty;
import im.toduck.global.log.properties.LogProperty;
import lombok.extern.slf4j.Slf4j;

/**
 * {@code LocalLogAspect}는 로컬 환경에서 실행되는 AOP 기반 로깅을 처리하는 클래스입니다.
 * <p>
 * 이 클래스는 {@link LogExplanations} 어노테이션이 적용된 메서드에 대한 로깅을 수행하며,
 * 호출 및 성공에 대한 정보를 로그로 기록합니다.
 * 로깅은 {@link InfoLogProperty}를 사용하여 로그 메시지를 구성합니다.
 * </p>
 * <h2>사용 목적</h2>
 * <p>
 * 로컬 환경에서 디버깅 목적으로 메서드 호출과 결과를 기록하여 개발자가 실행 흐름을 쉽게 추적할 수 있도록 돕습니다.
 * </p>
 * <h2>주요 기능</h2>
 * <ul>
 *     <li>메서드 호출 시, 호출 정보를 로그로 남깁니다.</li>
 *     <li>메서드 실행이 성공적으로 완료되면 결과 정보를 로그로 남깁니다.</li>
 * </ul>
 *
 * <h2>적용 대상</h2>
 * <p>
 * 이 AOP 클래스는 {@code local} 프로파일에서만 활성화되어 로컬 개발 환경에서 로깅 기능이 활성화됩니다.
 * </p>
 **/
@Aspect
@Component
@Slf4j
@Profile("local")
public class LocalLogAspect {

	@Before("im.toduck.global.aop.log.pointcut.LogPointCut.debugLogPointcut()")
	public void controllerLogBefore(JoinPoint joinPoint) throws Throwable {
		MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
		LogProperty logProperty = InfoLogProperty.of(
			getLogDescription(methodSignature),
			joinPoint,
			methodSignature.getName()
		);
		log.info("호출 {}", logProperty);
	}

	@AfterReturning(pointcut = "im.toduck.global.aop.log.pointcut.LogPointCut.debugLogPointcut()",
		returning = "result")
	public void logAfterReturning(JoinPoint joinPoint, Object result) {
		MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
		LogProperty logProperty = InfoLogProperty.of(
			getLogDescription(methodSignature),
			joinPoint,
			methodSignature.getName(),
			Optional.ofNullable(result)
		);
		log.info("성공 {} ", logProperty);
	}

	private String getLogDescription(MethodSignature methodSignature) {
		String description = "";
		if (methodSignature.getMethod().isAnnotationPresent(LogExplanations.class)) {
			GeneralLogExplanation[] general = methodSignature.getMethod()
				.getAnnotation(LogExplanations.class)
				.general();
			if (general.length > 0) {
				description = general[0].description();
			}
		}
		return description;
	}

}
