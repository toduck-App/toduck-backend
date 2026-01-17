package im.toduck.domain.badge.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "대표 뱃지 설정 요청")
public record RepresentativeBadgeRequest(
	@Schema(description = "설정할 뱃지 ID", example = "1")
	@NotNull(message = "뱃지 ID는 필수입니다.")
	Long badgeId
) {
}
