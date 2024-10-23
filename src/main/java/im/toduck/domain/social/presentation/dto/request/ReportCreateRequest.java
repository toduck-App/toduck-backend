package im.toduck.domain.social.presentation.dto.request;

import im.toduck.domain.social.persistence.entity.ReportType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReportCreateRequest(
	@NotNull(message = "신고 유형은 필수 입력 항목입니다.")
	@Schema(description = "신고 유형", example = "OTHER")
	ReportType reportType,

	@Size(max = 255, message = "사유는 255자 이하여야 합니다.")
	@Schema(description = "기타 신고 사유 (ReportType이 'OTHER'인 경우에만 사용)", example = "기타 사유 입력")
	String reason,

	@Schema(description = "작성자 차단 여부", example = "true")
	boolean blockAuthor
) {

	@AssertTrue(message = "신고 유형이 'OTHER'일 때 사유는 필수 입력 항목입니다.")
	public boolean isReasonRequiredForOtherType() {
		if (reportType == ReportType.OTHER) {
			return reason != null && !reason.isBlank();
		}
		return true;
	}

	@AssertTrue(message = "신고 유형이 'OTHER'가 아닌 경우 사유는 입력할 수 없습니다.")
	public boolean isReasonNotAllowedForNonOtherType() {
		if (reportType != ReportType.OTHER) {
			return reason == null || reason.isBlank();
		}
		return true;
	}
}
