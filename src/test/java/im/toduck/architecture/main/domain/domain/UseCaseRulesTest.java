package im.toduck.architecture.main.domain.domain;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static im.toduck.architecture.main.domain.ArchitectureConstants.Layer.*;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import im.toduck.global.annotation.UseCase;
import im.toduck.global.lock.DistributedLock;

@AnalyzeClasses(
	packages = {"im.toduck.domain", "im.toduck.global.lock"},
	importOptions = ImportOption.DoNotIncludeTests.class
)
public class UseCaseRulesTest {
	@ArchTest
	static final ArchRule UseCase_클래스는_UseCase_어노테이션을_가진다 =
		classes()
			.that().resideInAPackage(USECASE.getFullPackageName())
			.and().areTopLevelClasses()
			.and().doNotHaveSimpleName("*$*")
			.should().beAnnotatedWith(UseCase.class);

	@ArchTest
	static final ArchRule DistributedLock_메서드는_UseCase_클래스에서만_호출된다 =
		methods()
			.that().areDeclaredIn(DistributedLock.class)
			.should().onlyBeCalled().byClassesThat().resideInAPackage(USECASE.getFullPackageName())
			.orShould().beDeclaredIn(DistributedLock.class)
			.because("분산 락(DistributedLock) 메서드는 오직 UseCase 계층에서만 호출되어야 합니다.");
}
