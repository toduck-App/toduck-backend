package im.toduck.domain.backoffice.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "버전 체크 응답")
@Builder
public record VersionCheckResponse(
	@Schema(description = "업데이트 상태", example = "FORCE",
		allowableValues = {"FORCE", "RECOMMENDED", "NONE"})
	String updateStatus,

	@Schema(description = "최신 버전", example = "1.5.0")
	String latestVersion
) {
}
