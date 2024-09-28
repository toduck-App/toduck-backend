package im.toduck.global.aop.log.pointcut;

import org.aspectj.lang.annotation.Pointcut;

public class LogPointCut {
	@Pointcut("execution(* im.toduck.domain..*.*(..))")
	public void domainLoggingPointcut() {
	}

	@Pointcut("execution(* im.toduck.global..*.*(..)) && @annotation(org.springframework.stereotype.Component)")
	public void globalLoggingPointcut() {
	}
}
