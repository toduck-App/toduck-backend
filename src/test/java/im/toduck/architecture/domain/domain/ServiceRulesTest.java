package im.toduck.architecture.domain.domain;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static im.toduck.architecture.domain.ArchitectureConstants.Layer.*;

import org.springframework.stereotype.Service;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "im.toduck.domain", importOptions = ImportOption.DoNotIncludeTests.class)
public class ServiceRulesTest {
	@ArchTest
	static final ArchRule Service_클래스는_Service_어노테이션을_가진다 =
		classes()
			.that().resideInAPackage(SERVICE.getFullPackageName())
			.should().beAnnotatedWith(Service.class);
}
