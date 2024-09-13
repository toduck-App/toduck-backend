package im.toduck.global.validator;

import im.toduck.global.annotation.valid.PaginationLimit;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PaginationLimitValidator implements ConstraintValidator<PaginationLimit, Integer> {

	@Override
	public boolean isValid(Integer value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}

		return value > 0;
	}
}
