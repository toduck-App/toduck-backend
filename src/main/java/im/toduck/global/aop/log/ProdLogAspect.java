package im.toduck.global.aop.log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import im.toduck.global.annotation.log.ExceptionLogExplanation;
import im.toduck.global.annotation.log.GeneralLogExplanation;
import im.toduck.global.annotation.log.LogExplanations;
import im.toduck.global.exception.CommonException;
import im.toduck.global.log.properties.ExceptionLogProperty;
import im.toduck.global.log.properties.InfoLogProperty;
import im.toduck.global.log.properties.LogProperty;
import im.toduck.global.log.vo.LogLevel;
import lombok.extern.slf4j.Slf4j;

/**
 * ProdLogAspect 클래스는 애플리케이션의 로깅을 AOP를 통해 처리하는 역할을 합니다.
 * <p>
 * 이 클래스는 Controller 계층에서 메서드 호출 전, 성공 후, 그리고 예외 발생 시점에 각각 로그를 남기며,
 * 특히 Controller 메서드의 경우에만 INFO 로그 레벨을 사용하여 호출 정보를 기록합니다.
 * </p>
 * <ul>
 *     <li>{@code @Before} - Controller 메서드 호출 전 로그 기록</li>
 *     <li>{@code @AfterReturning} - Controller 메서드 성공 후 로그 기록 및 수행 시간 측정</li>
 *     <li>{@code @AfterThrowing} - 예외 발생 시 로그 기록 및 예외 정보 처리</li>
 * </ul>
 * <p>
 * Controller 메서드에서의 로그는 INFO 레벨로 기록되며, 이는 시스템 호출 흐름을 추적하고,
 * 요청이 정상적으로 처리되었는지를 확인하기 위한 목적으로 사용됩니다. 이때 메서드 실행 시간이 함께 기록되어
 * 성능 모니터링에 도움을 줍니다. 예외가 발생할 경우에는 WARN 또는 ERROR 로그 레벨로 기록됩니다.
 * </p>
 * <p>
 * 이 Aspect는 메서드에 정의된 어노테이션을 통해 로그 레벨과 설명을 동적으로 결정하며, {@link MDC}를 사용하여
 * 예외가 상위 메서드로 전파되어 로그가 중첩으로 찍히는 것을 방지합니다.
 * </p>
 *
 * <h2>주요 특징</h2>
 * <ul>
 *     <li>INFO 로그는 Controller 계층에서만 출력</li>
 *     <li>성공적인 메서드 호출 후 실행 시간 기록</li>
 *     <li>예외 발생 시 적절한 로그 레벨로 예외 정보 기록</li>
 * </ul>
 *
 * @author YourName
 * @version 1.0
 */
@Aspect
@Component
@Slf4j
public class ProdLogAspect {
	private ThreadLocal<Long> startTime = new ThreadLocal<>();

	@Around("im.toduck.global.aop.log.pointcut.LogPointCut.controllerInfoLoggingPointcut()")
	public Object controllerInfoLogAround(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
		LogProperty logProperty = InfoLogProperty.of(
			getLogDescription(methodSignature),
			joinPoint,
			methodSignature.getName()
		);

		startTime.set(System.currentTimeMillis());
		log.info("호출 {}", logProperty);

		Object result;
		try {
			result = joinPoint.proceed();
		} catch (CommonException commonException) {
			MDC.clear();
			startTime.remove();
			throw commonException;
		}

		logProperty = InfoLogProperty.of(
			getLogDescription(methodSignature),
			joinPoint,
			methodSignature.getName(),
			result
		);

		long elapsedTime = System.currentTimeMillis() - startTime.get();
		log.info("성공 (수행 시간: {} ms) {}", elapsedTime, logProperty);

		MDC.clear();
		startTime.remove();

		return result;
	}

	@AfterThrowing(pointcut = "im.toduck.global.aop.log.pointcut.LogPointCut.customExceptionLoggingPointcut()",
		throwing = "exception")
	public void logAfterThrowing(JoinPoint joinPoint, Exception exception) {
		if (MDC.get("isCustomThrow") != null) {
			return;
		}

		LogProperty logProperty;
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		if (exception instanceof CommonException commonException) {
			String description = getLogExplanationDescription(commonException, signature);

			logProperty = ExceptionLogProperty.of(description, joinPoint, commonException);
			startTime.remove();

			LogLevel logExplanationLogLevel = getLogExplanationLogLevel(commonException, signature);
			if (logExplanationLogLevel == LogLevel.ERROR) {
				log.error("실패 {}", logProperty);
			} else {
				log.warn("실패 {}", logProperty);
			}
			MDC.put("isCustomThrow", "true");
		}
	}

	private LogLevel getLogExplanationLogLevel(CommonException commonException, MethodSignature signature) {
		LogLevel logLevel = LogLevel.WARN;
		if (signature.getMethod().isAnnotationPresent(LogExplanations.class)) {
			ExceptionLogExplanation[] exceptionLogExplanations = signature.getMethod()
				.getAnnotation(LogExplanations.class)
				.exception();
			for (ExceptionLogExplanation exceptionLogExplanation : exceptionLogExplanations) {
				if (exceptionLogExplanation.code().getErrorCode() == commonException.getErrorCode()) {
					logLevel = exceptionLogExplanation.level();
				}
			}
		}
		return logLevel;
	}

	private String getLogExplanationDescription(CommonException commonException, MethodSignature signature) {
		String description = "";
		if (signature.getMethod().isAnnotationPresent(LogExplanations.class)) {
			ExceptionLogExplanation[] exceptionLogExplanations = signature.getMethod()
				.getAnnotation(LogExplanations.class)
				.exception();
			for (ExceptionLogExplanation exceptionLogExplanation : exceptionLogExplanations) {
				if (exceptionLogExplanation.code().getErrorCode() == commonException.getErrorCode()) {
					description = exceptionLogExplanation.description();
				}
			}
		}
		return description;
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
