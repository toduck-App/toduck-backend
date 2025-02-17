package im.toduck.domain.schedule.presentation.dto.response;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import im.toduck.domain.person.persistence.entity.PlanCategory;
import im.toduck.domain.schedule.persistence.entity.ScheduleRecord;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "일정 목록 응답 DTO")
@Builder
public record ScheduleHeadResponse(
	@Schema(description = "조회 시작 날짜", example = "2024-08-31")
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonFormat(pattern = "yyyy-MM-dd")
	LocalDate queryStartDate,

	@Schema(description = "조회 종료 날짜", example = "2024-09-01")
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonFormat(pattern = "yyyy-MM-dd")
	LocalDate queryEndDate,

	@Schema(description = "일정 목록")
	List<ScheduleHeadDto> scheduleHeadDtos
) {
	@Schema(description = "일정 DTO")
	@Builder
	public record ScheduleHeadDto(
		@Schema(description = "일정 Id", example = "1")
		Long scheduleId,

		@Schema(description = "일정 제목", example = "디자인팀 회의")
		String title,

		@Schema(description = "일정 기록 리스트")
		List<ScheduleRecordDto> scheduleRecordDto,

		@Schema(description = "일정 색상", example = "#FCDCDF")
		String color,

		@Schema(description = "일정 카테고리", example = "COMPUTER")
		PlanCategory category,

		@Schema(description = "종일 여부", example = "false")
		Boolean isAllDay,

		@Schema(description = "일정 시작 날짜", example = "2024-08-31")
		@JsonSerialize(using = LocalDateSerializer.class)
		@JsonFormat(pattern = "yyyy-MM-dd")
		LocalDate startDate,

		@Schema(description = "일정 종료 날짜", example = "2024-08-31")
		@JsonSerialize(using = LocalDateSerializer.class)
		@JsonFormat(pattern = "yyyy-MM-dd")
		LocalDate endDate,

		@Schema(description = "반복 요일", example = "[\"MONDAY\",\"TUESDAY\"]")
		List<DayOfWeek> daysOfWeek,

		@Schema(description = "일정 시간", example = "14:30")
		@JsonSerialize(using = LocalTimeSerializer.class)
		@JsonFormat(pattern = "HH:mm")
		LocalTime time,

		@Schema(description = "장소", example = "일정 장소")
		String location
	) {
		public record ScheduleRecordDto(
			@Schema(description = "일정 고유 id", example = "1")
			Long scheduleRecordId,
			@Schema(description = "일정 완료 여부", example = "false")
			Boolean isComplete,
			@Schema(description = "일정 기록 날짜", example = "2024-08-31")
			@JsonSerialize(using = LocalDateSerializer.class)
			@JsonFormat(pattern = "yyyy-MM-dd")
			LocalDate recordDate
		) {
			public static ScheduleRecordDto from(ScheduleRecord scheduleRecord) {
				return new ScheduleRecordDto(scheduleRecord.getId(), scheduleRecord.getIsCompleted(),
					scheduleRecord.getRecordDate());
			}
		}
	}
}
