package im.toduck.domain.social.presentation.dto.request;

import im.toduck.domain.social.persistence.entity.ReportType;
import im.toduck.global.annotation.valid.ValidReportReason;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@ValidReportReason
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

}
