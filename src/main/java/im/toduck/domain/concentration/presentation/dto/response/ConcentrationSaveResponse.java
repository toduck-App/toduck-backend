package im.toduck.domain.concentration.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ConcentrationSaveResponse(
	@Schema(description = "저장된 집중 ID", example = "1")
	Long concentrationId
) {
}
