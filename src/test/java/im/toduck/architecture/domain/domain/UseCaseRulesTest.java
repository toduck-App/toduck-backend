package im.toduck.architecture.domain.domain;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static im.toduck.architecture.domain.ArchitectureConstants.Layer.*;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import im.toduck.global.annotation.UseCase;

@AnalyzeClasses(packages = "im.toduck.domain", importOptions = ImportOption.DoNotIncludeTests.class)
public class UseCaseRulesTest {
	@ArchTest
	static final ArchRule UseCase_클래스는_UseCase_어노테이션을_가진다 =
		classes()
			.that().resideInAPackage(ENTITY.getFullPackageName())
			.should().beAnnotatedWith(UseCase.class);
}
