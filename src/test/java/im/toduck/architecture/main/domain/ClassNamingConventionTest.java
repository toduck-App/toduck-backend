package im.toduck.architecture.main.domain;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static im.toduck.architecture.main.domain.ArchitectureConstants.Layer.*;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "im.toduck.domain", importOptions = ImportOption.DoNotIncludeTests.class)
public class ClassNamingConventionTest {

	@ArchTest
	static final ArchRule 컨트롤러_클래스_네이밍_규칙을_준수한다 = classes()
		.that().resideInAPackage(CONTROLLER.getFullPackageName())
		.should().haveSimpleNameEndingWith("Controller");

	@ArchTest
	static final ArchRule 유스케이스_클래스_네이밍_규칙을_준수한다 = classes()
		.that().resideInAPackage(USECASE.getFullPackageName())
		.should().haveSimpleNameEndingWith("UseCase");

	@ArchTest
	static final ArchRule 서비스_클래스_네이밍_규칙을_준수한다 = classes()
		.that().resideInAPackage(SERVICE.getFullPackageName())
		.should().haveSimpleNameEndingWith("Service");

	@ArchTest
	static final ArchRule 레포지토리_클래스_네이밍_규칙을_준수한다 = classes()
		.that().resideInAPackage(REPOSITORY.getFullPackageName())
		.should().haveSimpleNameEndingWith("Repository")
		.orShould().haveSimpleNameEndingWith("RepositoryCustom")
		.orShould().haveSimpleNameEndingWith("RepositoryCustomImpl");

	@ArchTest
	static final ArchRule 매퍼_클래스_네이밍_규칙을_준수한다 = classes()
		.that().resideInAPackage(MAPPER.getFullPackageName())
		.and().areTopLevelClasses()
		.should().haveSimpleNameEndingWith("Mapper");

	@ArchTest
	static final ArchRule DTO_클래스_네이밍_규칙을_준수한다 = classes()
		.that().resideInAPackage(DTO.getFullPackageName())
		.and().areTopLevelClasses()
		.should().haveSimpleNameEndingWith("Dto")
		.orShould().haveSimpleNameEndingWith("Request")
		.orShould().haveSimpleNameEndingWith("Response")
		.orShould().haveSimpleNameEndingWith("JwtPair");
}
