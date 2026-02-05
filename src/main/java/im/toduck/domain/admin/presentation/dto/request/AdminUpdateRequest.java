package im.toduck.domain.admin.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "관리자 수정 요청 DTO")
public record AdminUpdateRequest(
	@Schema(description = "관리자 표시명", example = "토덕 관리자")
	String displayName
) {
}
