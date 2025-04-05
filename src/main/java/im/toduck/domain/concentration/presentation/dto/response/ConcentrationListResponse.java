package im.toduck.domain.concentration.presentation.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "월별 집중 목록 응답")
@Builder
public record ConcentrationListResponse(
	@Schema(description = "일기 목록")
	List<ConcentrationResponse> concentrationDtos
) {
	public static ConcentrationListResponse from(List<ConcentrationResponse> concentrations) {
		return ConcentrationListResponse.builder()
			.concentrationDtos(concentrations)
			.build();
	}
}
