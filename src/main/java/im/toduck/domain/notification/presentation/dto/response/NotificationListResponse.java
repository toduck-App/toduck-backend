package im.toduck.domain.notification.presentation.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "알림 목록 응답 DTO")
@Builder
public record NotificationListResponse(
	@Schema(description = "알림 목록")
	List<NotificationDto> notifications
) {
}
