package im.toduck.domain.social.presentation.dto.response;

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

@Schema(description = "사용자 프로필 루틴 목록 응답 DTO")
@Builder
public record UserProfileRoutineListResponse(
	@Schema(description = "루틴 목록")
	List<UserProfileRoutineResponse> routines
) {
	@Schema(description = "사용자 프로필 루틴 목록 내부 DTO")
	@Builder
	public record UserProfileRoutineResponse(
		@Schema(description = "루틴 Id", example = "1")
		Long routineId,

		@Schema(description = "루틴 카테고리", example = "MEDICINE")
		PlanCategory category,

		@Schema(description = "루틴 색상(null 이면 없는 색상)", example = "#FCDCDF")
		String color,

		@Schema(description = "루틴 제목", example = "하루 물 1L 이상 마시기")
		String title,

		@Schema(description = "루틴 메모", example = "눈 뜨자마자 한 잔")
		String memo,

		@Schema(description = "루틴 공유 수", example = "455")
		int sharedCount,

		@JsonSerialize(using = DayOfWeekListSerializer.class)
		@Schema(description = "반복 요일", example = "[\"MONDAY\",\"TUESDAY\"]")
		List<DayOfWeek> daysOfWeek,

		@JsonSerialize(using = LocalTimeSerializer.class)
		@JsonFormat(pattern = "HH:mm")
		@Schema(description = "루틴 시간 (null 이면 종일 루틴)", type = "string", example = "07:30")
		LocalTime time
	) {
	}
}
