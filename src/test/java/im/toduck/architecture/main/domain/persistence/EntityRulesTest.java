package im.toduck.architecture.main.domain.persistence;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static im.toduck.architecture.main.domain.ArchitectureConstants.Layer.*;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@AnalyzeClasses(packages = "im.toduck.domain", importOptions = ImportOption.DoNotIncludeTests.class)
public class EntityRulesTest {
	@ArchTest
	static final ArchRule Entity_클래스는_Entity_관련_어노테이션을_가진다 =
		classes()
			.that().resideInAPackage(ENTITY.getFullPackageName())
			.and().haveSimpleNameNotStartingWith("Q")
			.and().areTopLevelClasses()
			.and().areNotEnums()
			.should().beAnnotatedWith(Entity.class)
			.andShould().beAnnotatedWith(Table.class);
}
