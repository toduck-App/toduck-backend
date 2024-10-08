package im.toduck.global.annotation.valid;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import im.toduck.global.validator.ReportReasonValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = ReportReasonValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidReportReason {
	String message() default "신고 사유가 'OTHER(기타)'일 때는 reason을 반드시 입력해야 하며, 'OTHER'가 아닐 때는 reason이 포함되어서는 안됩니다.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
