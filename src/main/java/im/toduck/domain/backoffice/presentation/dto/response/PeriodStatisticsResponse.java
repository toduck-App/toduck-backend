package im.toduck.domain.backoffice.presentation.dto.response;

import java.time.LocalDate;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import im.toduck.domain.backoffice.persistence.entity.StatisticsType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "기간별 통계 응답 DTO")
@Builder
public record PeriodStatisticsResponse(
	@Schema(description = "통계 타입별 기간 내 개수",
		example = "{\"NEW_USERS\": 123, \"DELETED_USERS\": 45, \"NEW_DIARIES\": 234}")
	Map<StatisticsType, Long> statistics,

	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Schema(description = "조회 시작 날짜", example = "2024-01-01")
	LocalDate startDate,

	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Schema(description = "조회 종료 날짜", example = "2024-01-31")
	LocalDate endDate
) {
}
