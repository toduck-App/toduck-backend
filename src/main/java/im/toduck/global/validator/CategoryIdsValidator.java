package im.toduck.global.validator;

import java.util.List;

import im.toduck.global.annotation.valid.ValidCategoryIds;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CategoryIdsValidator implements ConstraintValidator<ValidCategoryIds, List<Long>> {

	@Override
	public boolean isValid(List<Long> value, ConstraintValidatorContext context) {
		return value == null || !value.isEmpty();
	}
}
