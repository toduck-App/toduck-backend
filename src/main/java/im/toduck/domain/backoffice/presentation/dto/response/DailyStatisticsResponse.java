package im.toduck.domain.backoffice.presentation.dto.response;

import java.time.LocalDate;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import im.toduck.domain.backoffice.persistence.entity.StatisticsType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "일일 통계 응답")
@Builder
public record DailyStatisticsResponse(
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Schema(description = "날짜", example = "2024-01-01")
	LocalDate date,

	@Schema(description = "통계 타입별 카운트")
	Map<StatisticsType, Long> counts
) {
}
