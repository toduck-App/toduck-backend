package im.toduck.domain.schedule.presentation.dto.request;

import static im.toduck.global.regex.PlanRegex.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

import im.toduck.domain.person.persistence.entity.PlanCategory;
import im.toduck.domain.schedule.persistence.vo.ScheduleAlram;
import im.toduck.global.serializer.DayOfWeekListDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ScheduleCreateRequest(
	@Schema(description = "일정 제목", example = "회사 미팅")
	@NotBlank(message = "일정 제목을 입력해주세요.")
	@Size(max = 20, message = "제목은 40자를 초과할 수 없습니다.") // TODO: 확정 필요
	String title,

	@Schema(description = "일정 카테고리", example = "COMPUTER")
	@NotNull(message = "일정 카테고리를 선택해주세요.")
	PlanCategory category,

	@Schema(description = "루틴 색상 (색상 없으면 null)", example = "#FF5733")
	@Pattern(regexp = HEX_COLOR_CODE_REGEX, message = "색상은 유효한 Hex code 여야 합니다.")
	String color,

	@Schema(description = "일정 날짜", example = "2025-01-01")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@NotNull(message = "일정 시작 날짜를 입력해주세요.")
	LocalDate startDate,

	@Schema(description = "일정 종료 날짜(null 이면 기간 일정이 아님)", example = "2025-01-10")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@NotNull(message = "일정 끝 날짜를 입력해주세요.")
	LocalDate endDate,

	@Schema(description = "종일 일정 여부", example = "true")
	@NotNull(message = "종일 일정 여부를 선택해주세요.")
	Boolean isAllDay,

	@Schema(description = "일정 시간 (종일 일정이면 null)", example = "10:30")
	LocalTime time,

	@Schema(description = "알람 시간 (null 이면 알람을 보내지 않음)", example = "TEN_MINUTE")
	ScheduleAlram alarm,

	@JsonDeserialize(using = DayOfWeekListDeserializer.class)
	@NotEmpty(message = "반	복 요일은 최소 하나 이상 선택되어야 합니다.")
	@Schema(description = "반복 요일", example = "[\"MONDAY\",\"TUESDAY\"]")
	List<DayOfWeek> daysOfWeek,

	@Schema(description = "일정 장소", example = "서울시 강남구")
	@Size(max = 40, message = "장소는 40자를 넘을 수 없습니다.")
	String location,

	@Schema(description = "메모", example = "30분 동안 조깅하기")
	@Size(max = 40, message = "메모는 40자를 넘을 수 없습니다.")
	String memo
) {
}
