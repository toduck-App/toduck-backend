package im.toduck.domain.routine.presentation.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "여러 날짜에 대한 본인 루틴기록 목록 응답 DTO")
@Builder
public record MyRoutineRecordReadMultipleDatesResponse(
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonFormat(pattern = "yyyy-MM-dd")
	@Schema(description = "조회 시작 날짜", example = "2024-08-31")
	LocalDate startDate,

	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonFormat(pattern = "yyyy-MM-dd")
	@Schema(description = "조회 종료 날짜", example = "2024-09-06")
	LocalDate endDate,

	@Schema(description = "날짜별 루틴 목록")
	List<MyRoutineRecordReadListResponse> dateRoutines
) {
}
