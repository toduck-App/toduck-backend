package im.toduck.domain.concentration.presentation.dto.response;

import java.util.List;

import im.toduck.domain.concentration.common.mapper.ConcentrationMapper;
import im.toduck.domain.concentration.persistence.entity.Concentration;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "월별 집중 목록 응답")
@Builder
public record ConcentrationListResponse(
	@Schema(description = "일기 목록")
	List<ConcentrationResponse> concentrationDtos
) {
	public static ConcentrationListResponse from(List<Concentration> concentrations) {
		List<ConcentrationResponse> dtos = concentrations.stream()
			.map(ConcentrationMapper::fromConcentration)
			.toList();

		return ConcentrationListResponse.builder()
			.concentrationDtos(dtos)
			.build();
	}
}
