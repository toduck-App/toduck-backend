package im.toduck.domain.backoffice.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "전체 통계 응답 DTO")
@Builder
public record OverallStatisticsResponse(
	@Schema(description = "전체 회원 수", example = "1234")
	long totalUserCount,

	@Schema(description = "전체 일기 수", example = "5678")
	long totalDiaryCount,

	@Schema(description = "전체 루틴 수", example = "9012")
	long totalRoutineCount,

	@Schema(description = "일기 작성자 수 (중복 제거)", example = "567")
	long activeDiaryWritersCount,

	@Schema(description = "루틴 사용자 수 (중복 제거)", example = "890")
	long activeRoutineUsersCount
) {
}
