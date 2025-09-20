package im.toduck.domain.backoffice.presentation.dto.response;

import java.util.Map;

import im.toduck.domain.backoffice.persistence.entity.StatisticsType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "전체 통계 응답 DTO")
@Builder
public record OverallStatisticsResponse(
	@Schema(description = "통계 타입별 전체 개수",
		example = "{\"NEW_USERS\": 1234, \"NEW_DIARIES\": 5678, \"NEW_ROUTINES\": 9012}")
	Map<StatisticsType, Long> statistics
) {
}
