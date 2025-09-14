package im.toduck.architecture.main.domain;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.*;
import static im.toduck.architecture.main.domain.ArchitectureConstants.Layer.*;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.importer.Location;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
	packages = "im.toduck.domain",
	importOptions = {
		ImportOption.DoNotIncludeTests.class,
		LayeredArchitectureTest.NotificationPackageIgnore.class,
		LayeredArchitectureTest.RoutineEventPackageIgnore.class,
		LayeredArchitectureTest.BackofficeEventPackageIgnore.class
	}
)
public class LayeredArchitectureTest {
	// notification 패키지 제외를 위한 커스텀 ImportOption
	public static class NotificationPackageIgnore implements ImportOption {
		@Override
		public boolean includes(Location location) {
			return !location.contains("notification");
		}
	}

	// routine.domain.event 패키지 제외를 위한 커스텀 ImportOption
	public static class RoutineEventPackageIgnore implements ImportOption {
		@Override
		public boolean includes(Location location) {
			return !location.contains("routine/domain/event");
		}
	}

	// backoffice.domain.event 패키지 제외를 위한 커스텀 ImportOption
	public static class BackofficeEventPackageIgnore implements ImportOption {
		@Override
		public boolean includes(Location location) {
			return !location.contains("backoffice/domain/event");
		}
	}

	@ArchTest
	static final ArchRule 레이어_의존성_규칙을_준수한다 = layeredArchitecture()
		.consideringAllDependencies()
		.layer(CONTROLLER.name()).definedBy(CONTROLLER.getFullPackageName())
		.layer(DTO.name()).definedBy(DTO.getFullPackageName())
		.layer(SERVICE.name()).definedBy(SERVICE.getFullPackageName())
		.layer(USECASE.name()).definedBy(USECASE.getFullPackageName())
		.layer(REPOSITORY.name()).definedBy(REPOSITORY.getFullPackageName())
		.layer(ENTITY.name()).definedBy(ENTITY.getFullPackageName())
		.layer(MAPPER.name()).definedBy(MAPPER.getFullPackageName())

		.whereLayer(CONTROLLER.name()).mayNotBeAccessedByAnyLayer()
		.whereLayer(SERVICE.name()).mayOnlyBeAccessedByLayers(USECASE.name())
		.whereLayer(USECASE.name()).mayOnlyBeAccessedByLayers(CONTROLLER.name())
		.whereLayer(REPOSITORY.name()).mayOnlyBeAccessedByLayers(SERVICE.name())
		.whereLayer(ENTITY.name())
		.mayOnlyBeAccessedByLayers(
			SERVICE.name(), USECASE.name(), REPOSITORY.name(), MAPPER.name(), ENTITY.name(), DTO.name()
		)
		.whereLayer(MAPPER.name()).mayOnlyBeAccessedByLayers(SERVICE.name(), USECASE.name())

		// QueryDSL 제외
		.ignoreDependency(JavaClass.Predicates.simpleNameStartingWith("Q"),
			JavaClass.Predicates.resideInAnyPackage(".."))
		.ignoreDependency(
			JavaClass.Predicates.resideInAPackage("..global..")
				.or(JavaClass.Predicates.resideInAPackage("..common.dto..")),
			JavaClass.Predicates.resideInAnyPackage("..")
		);

	@ArchTest
	static final ArchRule 오직_Entity_레이어의_enum_만_DTO_에서_사용될_수_있다 =
		classes()
			.that()
			.resideInAPackage(ENTITY.getFullPackageName())
			.and()
			.areTopLevelClasses()
			.and()
			.areEnums()
			.should()
			.onlyBeAccessed()
			.byAnyPackage(ENTITY.getFullPackageName(), DTO.getFullPackageName(), MAPPER.getFullPackageName());
}
