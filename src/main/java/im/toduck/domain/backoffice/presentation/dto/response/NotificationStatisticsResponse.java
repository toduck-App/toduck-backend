package im.toduck.domain.backoffice.presentation.dto.response;

import java.util.Map;

import im.toduck.domain.notification.persistence.entity.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "백오피스 알림 발송 통계 응답 DTO")
@Builder
public record NotificationStatisticsResponse(
	@Schema(description = "전체 알림 발송 개수", example = "15234")
	long totalNotificationsSent,

	@Schema(description = "오늘 알림 발송 개수", example = "342")
	long todayNotificationsSent,

	@Schema(description = "알림 유형별 발송 개수", example = """
		{
			"COMMENT": 5230,
			"LIKE_POST": 3421,
			"FOLLOW": 2103,
			"ROUTINE_REMINDER": 1234,
			"DIARY_REMINDER": 987,
			"BROADCAST": 456
		}
		""")
	Map<NotificationType, Long> notificationCountsByType
) {
}
