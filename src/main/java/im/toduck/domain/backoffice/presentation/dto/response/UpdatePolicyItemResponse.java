package im.toduck.domain.backoffice.presentation.dto.response;

import im.toduck.domain.backoffice.persistence.entity.UpdateType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "개별 버전 정책 응답")
@Builder
public record UpdatePolicyItemResponse(
	@Schema(description = "버전 ID", example = "1")
	Long id,

	@Schema(description = "버전", example = "1.5.0")
	String version,

	@Schema(description = "업데이트 타입", example = "LATEST")
	UpdateType updateType
) {
}
