package im.toduck.domain.notification.presentation.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import im.toduck.domain.notification.persistence.entity.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "알림 정보 DTO")
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record NotificationDto(
	@Schema(description = "알림 ID", example = "1")
	Long id,

	@Schema(description = "알림 타입", example = "COMMENT")
	NotificationType type,

	@Schema(description = "알림 제목", example = "새로운 댓글")
	String title,

	@Schema(description = "알림 내용", example = "홍길동님이 내 게시물에 댓글을 남겼어요.")
	String body,

	@Schema(description = "액션 URL", example = "/social/42/1")
	String actionUrl,

	@Schema(description = "알림 데이터")
	Object data,

	@Schema(description = "읽음 여부", example = "false")
	Boolean isRead,

	@Schema(description = "생성 시간", example = "2025-05-08 12:30:45")
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	LocalDateTime createdAt
) {
}
