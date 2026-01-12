package im.toduck.domain.events.events.presentation.dto.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "이벤트 수정 요청 DTO")
public record EventsUpdateRequest(
	@Size(max = 63, message = "이벤트 이름은 63자를 초과할 수 없습니다.")
	@Schema(description = "이벤트 이름", example = "출석 이벤트")
	String eventName,

	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@Schema(description = "이벤트 시작 일시", example = "2025-09-21T12:00:00")
	LocalDateTime startAt,

	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@Schema(description = "이벤트 종료 일시", example = "2025-09-30T23:59:59")
	LocalDateTime endAt,

	@Size(max = 1023, message = "썸네일 url은 1023자를 초과할 수 없습니다.")
	@Schema(description = "썸네일 url", example = "https://example.com/thumb.png")
	String thumbUrl,

	@Size(max = 63, message = "최소 앱 버전은 63자를 초과할 수 없습니다.")
	@Schema(description = "최소 앱 버전", example = "1.0.0")
	String appVersion,

	@Schema(description = "버튼 표시 여부", example = "true")
	Boolean buttonVisible,

	@Size(max = 63, message = "버튼 내용은 63자를 초과할 수 없습니다.")
	@Schema(description = "버튼 내용", example = "당첨 확인하기")
	String buttonText
) {

}
