package im.toduck.domain.routine.presentation.dto.response;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import im.toduck.domain.person.persistence.entity.PlanCategory;
import im.toduck.global.serializer.DayOfWeekListSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "루틴 상세조회 응답 DTO")
@Builder
public record RoutineDetailResponse(
	@Schema(description = "루틴 Id", example = "1")
	Long routineId,

	@Schema(description = "루틴 카테고리", example = "PENCIL")
	PlanCategory category,

	@Schema(description = "루틴 색상(null 이면 없는 색상)", example = "#FCDCDF")
	String color,

	@Schema(description = "루틴 제목", example = "디자인팀 회의")
	String title,

	@JsonSerialize(using = LocalTimeSerializer.class)
	@JsonFormat(pattern = "HH:mm")
	@Schema(description = "루틴 시간(null 이면 종일 루틴)", example = "14:30")
	LocalTime time,

	@JsonSerialize(using = DayOfWeekListSerializer.class)
	@Schema(description = "반복 요일", example = "[\"MONDAY\",\"TUESDAY\"]")
	List<DayOfWeek> daysOfWeek,

	@Schema(description = "루틴 메모", example = "눈 뜨자마자 이부자리 정리하는 사람은 성공한다더라..")
	String memo
) {
}
