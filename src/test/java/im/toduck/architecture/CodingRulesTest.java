package im.toduck.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.GeneralCodingRules.*;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "im.toduck", importOptions = ImportOption.DoNotIncludeTests.class)
public class CodingRulesTest {
	@ArchTest
	private final ArchRule 표준스트림에_접근하지_말아야한다 = NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;

	@ArchTest
	private void 메서드에서_표준스트림에_접근하지_말아야한다(JavaClasses classes) {
		noClasses().should(ACCESS_STANDARD_STREAMS).check(classes);
	}

	@ArchTest
	private final ArchRule 자바유틸로깅을_사용하지_말아야한다 = NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

	@ArchTest
	private final ArchRule JODATIME을_사용하지_말아야한다 = NO_CLASSES_SHOULD_USE_JODATIME;
}
