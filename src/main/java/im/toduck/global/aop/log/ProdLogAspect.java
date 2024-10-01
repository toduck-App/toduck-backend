package im.toduck.global.aop.log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import im.toduck.global.annotation.log.ExceptionLogExplanation;
import im.toduck.global.annotation.log.GeneralLogExplanation;
import im.toduck.global.annotation.log.LogExplanation;
import im.toduck.global.exception.CommonException;
import im.toduck.global.log.properties.ExceptionLogProperty;
import im.toduck.global.log.properties.InfoLogProperty;
import im.toduck.global.log.properties.LogProperty;
import im.toduck.global.log.vo.LogLevel;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class ProdLogAspect {
	private ThreadLocal<Long> startTime = new ThreadLocal<>();

	@Before("im.toduck.global.aop.log.pointcut.LogPointCut.controllerInfoLoggingPointcut()")
	public void controllerLogBefore(JoinPoint joinPoint) throws Throwable {
		MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
		LogProperty logProperty = InfoLogProperty.of(
			getLogDescription(methodSignature),
			joinPoint,
			methodSignature.getName()
		);

		startTime.set(System.currentTimeMillis());
		log.info("호출 {}", logProperty);
	}

	@AfterReturning(pointcut = "im.toduck.global.aop.log.pointcut.LogPointCut.controllerInfoLoggingPointcut()",
		returning = "result")
	public void logAfterReturning(JoinPoint joinPoint, Object result) {
		MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
		LogProperty logProperty = InfoLogProperty.of(
			getLogDescription(methodSignature),
			joinPoint,
			methodSignature.getName(),
			result
		);

		long elapsedTime = System.currentTimeMillis() - startTime.get();
		log.info("성공 (수행 시간: {} ms) {} ", elapsedTime, logProperty);

		MDC.clear();
		startTime.remove();
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
		if (signature.getMethod().isAnnotationPresent(LogExplanation.class)) {
			ExceptionLogExplanation[] exceptionLogExplanations = signature.getMethod()
				.getAnnotation(LogExplanation.class)
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
		if (signature.getMethod().isAnnotationPresent(LogExplanation.class)) {
			ExceptionLogExplanation[] exceptionLogExplanations = signature.getMethod()
				.getAnnotation(LogExplanation.class)
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
		if (methodSignature.getMethod().isAnnotationPresent(LogExplanation.class)) {
			GeneralLogExplanation[] general = methodSignature.getMethod().getAnnotation(LogExplanation.class).general();
			if (general.length > 0) {
				description = general[0].description();
			}
		}
		return description;
	}
}
