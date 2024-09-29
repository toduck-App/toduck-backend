package im.toduck.architecture.main.domain.common;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static im.toduck.architecture.main.domain.ArchitectureConstants.Layer.*;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "im.toduck.domain", importOptions = ImportOption.DoNotIncludeTests.class)
public class MapperRulesTest {
	@ArchTest
	static final ArchRule Mapper_클래스의_생성자는_private_이다 =
		constructors()
			.that().areDeclaredInClassesThat().resideInAPackage(MAPPER.getFullPackageName())
			.should().bePrivate();

	@ArchTest
	static final ArchRule Mapper_클래스의_모든_메서드는_static_이다 =
		methods()
			.that().areDeclaredInClassesThat().resideInAPackage(MAPPER.getFullPackageName())
			.should().beStatic();

	@ArchTest
	static final ArchRule Mapper_클래스의_정적_필드는_final_이다 =
		fields()
			.that().areDeclaredInClassesThat().resideInAPackage(MAPPER.getFullPackageName())
			.and().areStatic()
			.should().beFinal();
}
