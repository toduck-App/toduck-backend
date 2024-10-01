package im.toduck.global.aop.log.pointcut;

import org.aspectj.lang.annotation.Pointcut;

public class LogPointCut {
	@Pointcut("execution(* im.toduck.domain..*.*(..))")
	public void domainLoggingPointcut() {
	}

	@Pointcut("execution(* im.toduck.global..*.*(..)) && within(@org.springframework.stereotype.Component *)")
	public void globalLoggingPointcut() {
	}

	@Pointcut("domainLoggingPointcut() &&  within(@org.springframework.web.bind.annotation.RestController *)")
	public void controllerInfoLoggingPointcut() {
	}

	@Pointcut("domainLoggingPointcut() || globalLoggingPointcut()")
	public void customExceptionLoggingPointcut() {
	}

	@Pointcut("(domainLoggingPointcut() || globalLoggingPointcut()) && "
		+ "!within(@org.springframework.web.bind.annotation.RestController *)")
	public void debugLogPointcut() {
	}
}
