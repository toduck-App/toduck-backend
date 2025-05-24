package im.toduck.domain.concentration.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "특정 연월의 집중도 평균")
@Builder
public record ConcentrationPercentResponse(
	@Schema(description = "집중도 평균")
	Integer percent
) {
}
