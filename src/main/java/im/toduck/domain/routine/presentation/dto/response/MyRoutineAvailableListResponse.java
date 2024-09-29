package im.toduck.domain.routine.presentation.dto.response;

import java.util.List;

import im.toduck.domain.person.persistence.entity.PlanCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "활성화된 본인 루틴 목록 응답 DTO")
@Builder
public record MyRoutineAvailableListResponse(
	@Schema(description = "루틴 목록")
	List<MyRoutineAvailableResponse> routines

) {
	@Schema(description = "활성화된 본인 루틴 내부 DTO")
	@Builder
	public record MyRoutineAvailableResponse(
		@Schema(description = "루틴 Id", example = "1")
		Long routineId,

		@Schema(description = "루틴 카테고리", example = "PENCIL")
		PlanCategory category,

		@Schema(description = "루틴 제목", example = "기상 후 이부자리 정리!")
		String title,

		@Schema(description = "루틴 메모", example = "눈 뜨자마자 이부자리 정리하는 사람은 성공한다더라..")
		String memo
	) {
	}
}
