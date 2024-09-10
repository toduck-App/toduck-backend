package im.toduck.domain.routine.presentation.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "본인 루틴 목록 응답 DTO")
@Builder
public record MyRoutineReadListResponse(
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonFormat(pattern = "yyyy-MM-dd")
	@Schema(description = "조회 날짜", example = "2024-08-31")
	LocalDate queryDate,

	@Schema(description = "루틴 목록")
	List<MyRoutineReadResponse> routines

) {
	@Schema(description = "본인 루틴 목록 내부 DTO")
	@Builder
	public record MyRoutineReadResponse(
		@Schema(description = "루틴 Id", example = "1")
		Long routineId,

		@Schema(description = "루틴 색상(null 이면 없는 색상)", example = "#FCDCDF")
		String color,

		@JsonSerialize(using = LocalTimeSerializer.class)
		@JsonFormat(pattern = "HH:mm")
		@Schema(description = "루틴 시간(null 이면 종일 루틴)", example = "14:30")
		LocalTime time,

		@Schema(description = "루틴 제목", example = "디자인팀 회의")
		String title,

		@Schema(description = "루틴 완료 여부", example = "true")
		Boolean isCompleted
	) {
	}
}
