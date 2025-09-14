package im.toduck.domain.backoffice.presentation.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import im.toduck.domain.backoffice.persistence.entity.BroadcastNotificationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "브로드캐스트 알림 응답 DTO")
@Builder
public record BroadcastNotificationResponse(
	@Schema(description = "알림 ID", example = "1")
	Long id,

	@Schema(description = "알림 제목", example = "서비스 업데이트 안내")
	String title,

	@Schema(description = "알림 메시지", example = "새로운 기능이 추가되었습니다.")
	String message,

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	@Schema(description = "예약 발송 시간", example = "2024-12-25T10:00:00")
	LocalDateTime scheduledAt,

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	@Schema(description = "실제 발송 시간", example = "2024-12-25T10:00:05")
	LocalDateTime sentAt,

	@Schema(description = "알림 상태", example = "COMPLETED")
	BroadcastNotificationStatus status,

	@Schema(description = "상태 설명", example = "발송 완료")
	String statusDescription,

	@Schema(description = "대상 사용자 수", example = "1500")
	Integer targetUserCount,

	@Schema(description = "발송 완료 사용자 수", example = "1450")
	Integer sentUserCount,

	@Schema(description = "실패 사유", example = "일부 디바이스 토큰 만료")
	String failureReason,

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	@Schema(description = "생성 시간", example = "2024-12-24T15:30:00")
	LocalDateTime createdAt,

	@Schema(description = "취소 가능 여부", example = "false")
	boolean canCancel
) {
}
