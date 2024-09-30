package im.toduck.architecture.main.domain;

public final class ArchitectureConstants {

	public enum PackageName {
		PRESENTATION("presentation"),
		DOMAIN("domain"),
		PERSISTENCE("persistence"),
		COMMON("common");

		private final String value;

		PackageName(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	public enum Layer {
		API(PackageName.PRESENTATION, "api"),
		CONTROLLER(PackageName.PRESENTATION, "controller"),
		DTO(PackageName.PRESENTATION, "dto"),
		SERVICE(PackageName.DOMAIN, "service"),
		USECASE(PackageName.DOMAIN, "usecase"),
		REPOSITORY(PackageName.PERSISTENCE, "repository"),
		ENTITY(PackageName.PERSISTENCE, "entity"),
		MAPPER(PackageName.COMMON, "mapper");

		private final PackageName packageName;
		private final String name;

		Layer(PackageName packageName, String name) {
			this.packageName = packageName;
			this.name = name;
		}

		public String getFullPackageName() {
			return ".." + packageName.getValue() + "." + name + "..";
		}
	}

	private ArchitectureConstants() {
	}
}
