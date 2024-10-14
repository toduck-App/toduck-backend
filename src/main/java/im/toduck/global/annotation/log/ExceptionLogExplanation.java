package im.toduck.global.annotation.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import im.toduck.global.exception.ExceptionCode;
import im.toduck.global.log.vo.LogLevel;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExceptionLogExplanation {
	String description() default "";

	ExceptionCode code();

	LogLevel level() default LogLevel.WARN;
}
