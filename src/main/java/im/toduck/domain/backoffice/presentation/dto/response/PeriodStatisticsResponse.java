package im.toduck.domain.backoffice.presentation.dto.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "기간별 통계 응답 DTO")
@Builder
public record PeriodStatisticsResponse(
	@Schema(description = "신규 가입자 수", example = "123")
	long newUsersCount,

	@Schema(description = "탈퇴자 수", example = "45")
	long deletedUsersCount,

	@Schema(description = "신규 일기 수", example = "234")
	long newDiariesCount,

	@Schema(description = "신규 루틴 수", example = "67")
	long newRoutinesCount,

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
