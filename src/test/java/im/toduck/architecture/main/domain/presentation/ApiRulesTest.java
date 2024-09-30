package im.toduck.architecture.main.domain.presentation;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static im.toduck.architecture.main.domain.ArchitectureConstants.Layer.*;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import im.toduck.global.annotation.swagger.ApiResponseExplanations;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@AnalyzeClasses(packages = "im.toduck.domain", importOptions = ImportOption.DoNotIncludeTests.class)
public class ApiRulesTest {
	@ArchTest
	static final ArchRule API_클래스는_인터페이스이다 =
		classes()
			.that().resideInAPackage(API.getFullPackageName())
			.should().beInterfaces();

	@ArchTest
	static final ArchRule API_클래스는_Tag_어노테이션을_가진다 =
		classes()
			.that().resideInAPackage(API.getFullPackageName())
			.should().beAnnotatedWith(Tag.class);

	@ArchTest
	static final ArchRule API_메서드는_Operation_어노테이션을_가진다 =
		methods()
			.that().areDeclaredInClassesThat().resideInAPackage(API.getFullPackageName())
			.should().beAnnotatedWith(Operation.class);

	@ArchTest
	static final ArchRule API_메서드는_ApiResponseExplanations_어노테이션을_가진다 =
		methods()
			.that().areDeclaredInClassesThat().resideInAPackage(API.getFullPackageName())
			.should().beAnnotatedWith(ApiResponseExplanations.class);
}

