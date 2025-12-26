package im.toduck.domain.backoffice.presentation.dto.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "브로드캐스트 알림 생성 요청 DTO")
public record BroadcastNotificationCreateRequest(
	@NotBlank(message = "제목은 비어있을 수 없습니다.")
	@Size(max = 100, message = "제목은 100자를 초과할 수 없습니다.")
	@Schema(description = "알림 제목 ({@Username} 플레이스홀더 사용 가능)", example = "{@Username}님, 서비스 업데이트 안내")
	String title,

	@NotBlank(message = "메시지는 비어있을 수 없습니다.")
	@Size(max = 500, message = "메시지는 500자를 초과할 수 없습니다.")
	@Schema(description = "알림 메시지 ({@Username} 플레이스홀더 사용 가능)", example = "새로운 기능이 추가되었습니다. 확인해보세요!")
	String message,

	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Future(message = "예약 발송 시간은 현재 시간보다 미래여야 합니다.")
	@Schema(description = "예약 발송 시간 (null이면 즉시 발송)", example = "2024-12-25 10:00:00")
	LocalDateTime scheduledAt,

	@NotBlank(message = "액션 URL은 비어있을 수 없습니다.")
	@Size(max = 500, message = "액션 URL은 500자를 초과할 수 없습니다.")
	@Schema(description = "알림 클릭 시 이동할 딥링크", example = "toduck://diary")
	String actionUrl
) {
}
