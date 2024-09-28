package im.toduck.global.helper;

import org.slf4j.MDC;

import im.toduck.global.log.vo.LogLevel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomExceptionLogger {
	public static void warn(String message, Object... args) {
		log.warn(message, args);
		MDC.put("logLevel", "true");
	}

	public static void error(String message, Object... args) {
		log.error(message, args);
		MDC.put("logLevel", "true");
	}

	public static void info(String message, Object... args) {
		log.info(message, args);
		MDC.put("logLevel", "true");
	}

	public static void custom(LogLevel logLevel, String message, Object... args) {
		if (logLevel == LogLevel.ERROR) {
			error(message, args);
		} else if (logLevel == LogLevel.WARN) {
			warn(message, args);
		} else if (logLevel == LogLevel.INFO) {
			info(message, args);
		}
	}
}
