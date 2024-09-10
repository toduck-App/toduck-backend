package im.toduck.domain.routine.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "루틴 생성 응답 DTO")
@Builder
public record RoutineCreateResponse(
	@Schema(description = "생성된 루틴 Id", example = "1")
	Long routineId
) {
}
