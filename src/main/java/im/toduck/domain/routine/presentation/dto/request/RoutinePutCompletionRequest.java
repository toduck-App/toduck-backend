package im.toduck.domain.routine.presentation.dto.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "루틴 완료 상태 변경 요청 DTO")
public record RoutinePutCompletionRequest(
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonFormat(pattern = "yyyy-MM-dd")
	@NotBlank(message = "완료 상태를 변경할 일자는 null 일 수 없습니다.")
	@Schema(description = "완료 상태를 변경할 일자", example = "2024-09-01")
	LocalDate routineDate,

	@Schema(description = "완료/미완료 상태", example = "true")
	boolean isCompleted
) {
}
