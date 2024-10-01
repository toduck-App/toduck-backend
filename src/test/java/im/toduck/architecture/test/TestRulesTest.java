package im.toduck.architecture.test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "im.toduck", importOptions = ImportOption.OnlyIncludeTests.class)
public class TestRulesTest {

	@ArchTest
	public static final ArchRule 가독성을_위해_Junit을_사용하지_않는다 =
		noClasses()
			.should().accessClassesThat()
			.haveFullyQualifiedName(org.junit.Assert.class.getName())
			.because("Junit 대신 AssertJ를 사용하세요.");
}
