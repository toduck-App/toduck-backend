package im.toduck.domain.events.events.presentation.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "이벤트 수정 요청 DTO")
public record EventsUpdateRequest(
	@Schema(description = "이벤트 이름", example = "출석 이벤트")
	String eventName,

	@Schema(description = "이벤트 시작 일시", example = "2025-09-21T12:00:00")
	LocalDateTime startAt,

	@Schema(description = "이벤트 종료 일시", example = "2025-09-30T23:59:59")
	LocalDateTime endAt,

	@Schema(description = "썸네일 URL", example = "https://example.com/thumb.png")
	String thumbUrl,

	@Schema(description = "앱 버전", example = "1.0.0")
	String appVersion
) {

}
