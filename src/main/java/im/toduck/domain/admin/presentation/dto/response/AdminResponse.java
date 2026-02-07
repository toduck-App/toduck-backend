package im.toduck.domain.admin.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "관리자 응답")
public record AdminResponse(
	@Schema(description = "관리자 ID", example = "1")
	Long adminId,

	@Schema(description = "사용자 ID", example = "1")
	Long userId,

	@Schema(description = "관리자 표시명", example = "토덕 관리자")
	String displayName
) {
}
