package im.toduck.architecture.domain;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.*;
import static im.toduck.architecture.domain.ArchitectureConstants.Layer.*;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "im.toduck.domain", importOptions = ImportOption.DoNotIncludeTests.class)
public class LayeredArchitectureTest {

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

		.ignoreDependency(JavaClass.Predicates.simpleNameStartingWith("Q"),
			JavaClass.Predicates.resideInAnyPackage(".."))
		.ignoreDependency(JavaClass.Predicates.resideInAPackage("..global.."),
			JavaClass.Predicates.resideInAnyPackage(".."));

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
