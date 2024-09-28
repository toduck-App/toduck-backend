package im.toduck.global.aop.log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import im.toduck.global.annotation.log.GeneralLogExplanation;
import im.toduck.global.annotation.log.LogExplanation;
import im.toduck.global.log.properties.InfoLogProperty;
import im.toduck.global.log.properties.LogProperty;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class InfoLogAspect {
	@Before("im.toduck.global.aop.log.pointcut.LogPointCut.domainLoggingPointcut() "
		+ "|| im.toduck.global.aop.log.pointcut.LogPointCut.globalLoggingPointcut()")
	public void logBefore(JoinPoint joinPoint) throws Throwable {
		MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
		LogProperty logProperty = InfoLogProperty.of(
			getLogDescription(methodSignature),
			joinPoint,
			methodSignature.getName()
		);
		log.info("호출 {}", logProperty);
	}

	@AfterReturning(pointcut = "im.toduck.global.aop.log.pointcut.LogPointCut.domainLoggingPointcut() "
		+ "|| im.toduck.global.aop.log.pointcut.LogPointCut.globalLoggingPointcut()",
		returning = "result")
	public void logAfterReturning(JoinPoint joinPoint, Object result) {
		LogProperty logProperty = InfoLogProperty.of(
			getLogDescription((MethodSignature)joinPoint.getSignature()),
			joinPoint,
			((MethodSignature)joinPoint.getSignature()).getName(),
			result
		);
		log.info("성공 {}", logProperty);
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
