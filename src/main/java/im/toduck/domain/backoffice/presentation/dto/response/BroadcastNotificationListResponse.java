package im.toduck.domain.backoffice.presentation.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "브로드캐스트 알림 목록 응답 DTO")
@Builder
public record BroadcastNotificationListResponse(
	@Schema(description = "브로드캐스트 알림 목록")
	List<BroadcastNotificationResponse> notifications,

	@Schema(description = "총 알림 수", example = "25")
	int totalCount
) {
}
