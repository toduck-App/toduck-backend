package im.toduck.domain.events.events.presentation.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import im.toduck.domain.events.events.persistence.entity.Events;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "이벤트 목록 확인 응답")
public record EventsResponse(
	@Schema(description = "이벤트 ID")
	Long id,

	@Schema(description = "이벤트 이름")
	String eventName,

	@Schema(description = "시작일시")
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	LocalDateTime startAt,

	@Schema(description = "종료일시")
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	LocalDateTime endAt,

	@Schema(description = "썸네일 URL")
	String thumbUrl,

	@Schema(description = "앱 버전")
	String appVersion
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
				.build())
			.toList();
	}
}
