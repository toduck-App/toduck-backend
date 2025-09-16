package im.toduck.domain.backoffice.presentation.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "멀티 날짜 통계 응답 DTO")
@Builder
public record MultiDateStatisticsResponse(
	@Schema(description = "일별 통계 데이터 목록")
	List<DailyStatisticsData> statistics,

	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Schema(description = "시작 날짜", example = "2024-01-01")
	LocalDate startDate,

	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Schema(description = "종료 날짜", example = "2024-01-07")
	LocalDate endDate
) {
}
