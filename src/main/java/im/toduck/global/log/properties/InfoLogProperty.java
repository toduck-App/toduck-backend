package im.toduck.global.log.properties;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

public class InfoLogProperty extends LogProperty {

	private InfoLogProperty(String description, String className, String methodName, Object[] args) {
		super(description, className, methodName, args);
	}

	public static InfoLogProperty of(String description, JoinPoint joinPoint, String methodName) {
		return new InfoLogProperty(
			description,
			joinPoint.getSignature().getDeclaringTypeName(),
			methodName,
			joinPoint.getArgs()
		);
	}

	public static InfoLogProperty of(String description, JoinPoint joinPoint, String methodName,
		Optional<Object> result) {
		return new InfoLogProperty(
			description,
			joinPoint.getSignature().getDeclaringTypeName(),
			((MethodSignature)joinPoint.getSignature()).getName(),
			new Map[] {Map.of(
				"result", result.orElseGet(() -> "void")
			)}
		);
	}

	@Override
	public String toString() {
		return "{"
			+ "description='" + description
			+ ", className='" + className
			+ ", methodName='" + methodName
			+ ", args=" + Arrays.toString(args)
			+ '}';
	}
}
