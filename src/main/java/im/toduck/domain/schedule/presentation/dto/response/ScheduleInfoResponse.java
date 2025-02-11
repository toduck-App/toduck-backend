package im.toduck.domain.schedule.presentation.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import im.toduck.domain.person.persistence.entity.PlanCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ScheduleInfoResponse(
	@Schema(description = "일정 Id", example = "1")
	Long scheduleId,

	@Schema(description = "일정 제목", example = "디자인팀 회의")
	String title,

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

	@Schema(description = "일정 시간", example = "14:30")
	@JsonSerialize(using = LocalTimeSerializer.class)
	@JsonFormat(pattern = "HH:mm")
	LocalTime time,

	@Schema(description = "장소", example = "일정 장소")
	String location,

	@Schema(description = "메모", example = "일정 메모")
	String memo,

	@Schema(description = "일정 기록 고유 ID", example = "일정 기록 ID")
	Long scheduleRecordId,

	@Schema(description = "일정 완료 여부", example = "false")
	Boolean isComplete,

	@Schema(description = "일정 기록 날짜", example = "2024-08-31")
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonFormat(pattern = "yyyy-MM-dd")
	LocalDate recordDate

) {
}
