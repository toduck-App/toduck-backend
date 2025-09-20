package im.toduck.domain.backoffice.presentation.dto.request;

import im.toduck.domain.backoffice.persistence.entity.UpdateType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "개별 버전 정책 설정")
public record UpdatePolicyItemRequest(
	@NotNull(message = "버전 ID는 필수입니다.")
	@Positive(message = "버전 ID는 양수여야 합니다.")
	@Schema(description = "앱 버전 ID", example = "1")
	Long id,

	@NotNull(message = "업데이트 타입은 필수입니다.")
	@Schema(description = "업데이트 정책 타입", example = "LATEST",
		allowableValues = {"LATEST", "RECOMMENDED", "FORCE", "NONE"})
	UpdateType updateType
) {
}
