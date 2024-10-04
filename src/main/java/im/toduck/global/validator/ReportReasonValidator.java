package im.toduck.global.validator;

import im.toduck.domain.social.persistence.entity.ReportType;
import im.toduck.domain.social.presentation.dto.request.ReportCreateRequest;
import im.toduck.global.annotation.valid.ValidReportReason;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ReportReasonValidator implements ConstraintValidator<ValidReportReason, ReportCreateRequest> {

	@Override
	public boolean isValid(ReportCreateRequest request, ConstraintValidatorContext context) {
		if (request.reportType() == ReportType.OTHER) {
			return request.reason() != null && !request.reason().isBlank();
		}

		return request.reason() == null || request.reason().isBlank();
	}
}
