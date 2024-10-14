package im.toduck.global.log.properties;

import java.util.Arrays;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import im.toduck.global.exception.CommonException;

public class ExceptionLogProperty extends LogProperty {

	private ExceptionLogProperty(String description, String className, String methodName,
		CommonException commonException) {
		super();
		super.description = description;
		super.className = className;
		super.methodName = methodName;
		Map<String, String> map = Map.of(
			"httpStatus", commonException.getHttpStatus().toString(),
			"errorCode", String.valueOf(commonException.getErrorCode()),
			"message", commonException.getMessage()
		);
		super.args = new Map[] {map};
	}

	public static ExceptionLogProperty of(String description, JoinPoint joinPoint, CommonException commonException) {
		return new ExceptionLogProperty(
			description,
			joinPoint.getSignature().getDeclaringTypeName(),
			((MethodSignature)joinPoint.getSignature()).getName(),
			commonException
		);
	}

	@Override
	public String toString() {
		return "{"
			+ "description='" + description + '\''
			+ ", className='" + className + '\''
			+ ", methodName='" + methodName + '\''
			+ ", args=" + Arrays.toString(args)
			+ '}';
	}
}
