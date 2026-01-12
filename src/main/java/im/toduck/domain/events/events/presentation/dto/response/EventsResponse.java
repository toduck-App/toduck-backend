package im.toduck.domain.events.events.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import im.toduck.domain.events.events.persistence.entity.Events;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "이벤트 응답")
public record EventsResponse(
	@Schema(description = "이벤트 ID", example = "1")
	Long id,

	@Schema(description = "이벤트 이름", example = "댓글 이벤트")
	String eventName,

	@Schema(description = "시작일시")
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	LocalDateTime startAt,

	@Schema(description = "종료일시")
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	LocalDateTime endAt,

	@Schema(description = "썸네일 URL", example = "https://asdf.jpg")
	String thumbUrl,

	@Schema(description = "앱 버전", example = "1.0.1")
	String appVersion,

	@Schema(description = "버튼 표시 여부", example = "true")
	Boolean isButtonVisible,

	@Schema(description = "버튼 내용", example = "당첨 확인하기")
	String buttonText
) {
	public static List<EventsResponse> toEventsCheckResponse(List<Events> eventsList) {
		return eventsList.stream()
			.map(e -> EventsResponse.builder()
				.id(e.getId())
				.eventName(e.getEventName())
				.startAt(e.getStartAt())
				.endAt(e.getEndAt())
				.thumbUrl(e.getThumbUrl())
				.appVersion(e.getAppVersion())
				.isButtonVisible(e.getButtonVisible())
				.buttonText(e.getButtonText())
				.build())
			.toList();
	}
}
