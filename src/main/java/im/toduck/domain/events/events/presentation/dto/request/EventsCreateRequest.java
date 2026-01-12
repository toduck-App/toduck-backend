package im.toduck.domain.events.events.presentation.dto.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "이벤트 생성 요청 DTO")
public record EventsCreateRequest(
	@NotNull(message = "이벤트 이름은 비어있을 수 없습니다.")
	@Size(max = 63, message = "이벤트 이름은 63자를 초과할 수 없습니다.")
	@Schema(description = "이벤트 이름", example = "경험 공유 EVENT")
	String eventName,

	@NotNull(message = "시작 시간은 비어있을 수 없습니다.")
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@Schema(description = "시작 시간", example = "2025-09-22T00:00:00")
	LocalDateTime startAt,

	@NotNull(message = "종료 시간은 비어있을 수 없습니다.")
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@Schema(description = "종료 시간", example = "2025-09-23T23:59:59")
	LocalDateTime endAt,

	@NotNull(message = "썸네일 url은 비어있을 수 없습니다.")
	@Size(max = 1023, message = "썸네일 url은 1023자를 초과할 수 없습니다.")
	@Schema(description = "썸네일 url", example = "https://asdf.jpg")
	String thumbUrl,

	@NotNull(message = "최소 앱 버전은 비어있을 수 없습니다.")
	@Size(max = 63, message = "최소 앱 버전은 63자를 초과할 수 없습니다.")
	@Schema(description = "최소 앱 버전", example = "1.0.1")
	String appVersion,

	@NotNull(message = "버튼 표시 여부는 비어있을 수 없습니다.")
	@Schema(description = "버튼 표시 여부", example = "true")
	Boolean buttonVisible,

	@Size(max = 63, message = "버튼 내용은 63자를 초과할 수 없습니다.")
	@Schema(description = "버튼 내용", example = "당첨 확인하기")
	String buttonText
) {

}
