package im.toduck.domain.routine.presentation.dto.response;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import im.toduck.domain.person.persistence.entity.PlanCategory;
import im.toduck.global.serializer.DayOfWeekListSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "날짜별 본인 루틴기록 목록 응답 DTO")
@Builder
public record MyRoutineRecordReadListResponse(
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonFormat(pattern = "yyyy-MM-dd")
	@Schema(description = "조회 날짜", example = "2024-08-31")
	LocalDate queryDate,

	@Schema(description = "루틴 목록")
	List<MyRoutineReadResponse> routines

) {
	// FIXME: 클라이언트 요청으로 일부 필드 추가됨, 추후 간소화 필요
	@Schema(description = "본인 루틴 목록 내부 DTO")
	@Builder
	public record MyRoutineReadResponse(
		@Schema(description = "루틴 Id", example = "1")
		Long routineId,

		@Schema(description = "루틴 카테고리", example = "COMPUTER")
		PlanCategory category,

		@Schema(description = "루틴 색상(null 이면 없는 색상)", example = "#FCDCDF")
		String color,

		@Schema(description = "루틴 제목", example = "디자인팀 회의")
		String title,

		@JsonSerialize(using = LocalTimeSerializer.class)
		@JsonFormat(pattern = "HH:mm")
		@Schema(description = "루틴 시간(null 이면 종일 루틴)", example = "14:30")
		LocalTime time,

		@Schema(description = "루틴 공개/비공개 여부", example = "true")
		Boolean isPublic,

		@Schema(description = "이미 삭제된 루틴인지 여부, true 일 경우 이미 모 루틴이 삭제된 상태임", example = "true")
		Boolean isInDeletedState,

		@JsonSerialize(using = DayOfWeekListSerializer.class)
		@Schema(description = "반복 요일", example = "[\"MONDAY\",\"TUESDAY\"]")
		List<DayOfWeek> daysOfWeek,

		@Schema(description = "루틴 메모", example = "눈 뜨자마자 이부자리 정리하는 사람은 성공한다더라..")
		String memo,

		@Schema(description = "루틴 완료 여부", example = "true")
		Boolean isCompleted
	) {
	}
}
