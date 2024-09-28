package im.toduck.global.log.properties;

public abstract class LogProperty {
	String description;
	String className;
	String methodName;
	Object[] args;

	public LogProperty() {
	}

	public LogProperty(String description, String className, String methodName, Object[] args) {
		this.description = description;
		this.className = className;
		this.methodName = methodName;
		this.args = args;
	}
}
