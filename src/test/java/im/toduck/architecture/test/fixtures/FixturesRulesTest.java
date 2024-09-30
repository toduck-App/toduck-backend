package im.toduck.architecture.test.fixtures;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "im.toduck.fixtures", importOptions = ImportOption.OnlyIncludeTests.class)
public class FixturesRulesTest {

	@ArchTest
	public static final ArchRule Fixture의_public_메서드는_대문자와_언더바_조합이다 =
		methods()
			.that().arePublic()
			.should().haveNameMatching("[A-Z_]+");

	@ArchTest
	public static final ArchRule Fixture의_모든_메서드는_static이다 = methods().should().beStatic();

	@ArchTest
	public static final ArchRule 모든_Fixture_클래스명은_Fixtures로끝난다 =
		classes().should()
			.haveSimpleNameEndingWith("Fixtures");
}
