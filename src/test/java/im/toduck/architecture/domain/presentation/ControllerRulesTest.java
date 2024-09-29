package im.toduck.architecture.domain.presentation;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static im.toduck.architecture.domain.ArchitectureConstants.Layer.*;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "im.toduck.domain", importOptions = ImportOption.DoNotIncludeTests.class)
public class ControllerRulesTest {

	@ArchTest
	static final ArchRule Controller_메서드는_인증된_메서드만_포함한다 =
		methods()
			.that().areDeclaredInClassesThat().resideInAPackage(CONTROLLER.getFullPackageName())
			.and().arePublic()
			.should().beAnnotatedWith(PreAuthorize.class);

	@ArchTest
	static final ArchRule Controller_클래스는_Api를_구현한다 =
		classes()
			.that().resideInAPackage(CONTROLLER.getFullPackageName())
			.should().implement(JavaClass.Predicates.simpleNameEndingWith("Api"));

	@ArchTest
	static final ArchRule Controller_메서드는_ResponseEntity를_반환한다 =
		methods()
			.that().areDeclaredInClassesThat().resideInAPackage(CONTROLLER.getFullPackageName())
			.and().arePublic()
			.should().haveRawReturnType(ResponseEntity.class);

	@ArchTest
	static final ArchRule Controller_클래스는_RestController_어노테이션을_가진다 =
		classes()
			.that().resideInAPackage(CONTROLLER.getFullPackageName())
			.should().beAnnotatedWith(RestController.class);
}
