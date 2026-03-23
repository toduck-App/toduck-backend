package im.toduck.domain.admin.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "관리자 수정 요청 DTO")
public record AdminUpdateRequest(
	@NotBlank(message = "관리자 표시명은 비어있을 수 없습니다.")
	@Size(max = 255, message = "관리자 표시명은 255자를 초과할 수 없습니다.")
	@Schema(description = "관리자 표시명", example = "토덕 관리자")
	String displayName
) {
}
