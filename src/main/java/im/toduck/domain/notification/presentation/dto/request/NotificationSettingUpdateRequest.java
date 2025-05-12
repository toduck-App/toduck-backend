package im.toduck.domain.notification.presentation.dto.request;

import im.toduck.domain.notification.persistence.entity.NotificationMethod;
import io.swagger.v3.oas.annotations.media.Schema;

public record NotificationSettingUpdateRequest(
	@Schema(description = "전체 알림 활성화 여부", example = "true")
	boolean allEnabled,

	@Schema(description = "알림 방식", example = "SOUND_ONLY, VIBRATION_ONLY")
	NotificationMethod notificationMethod,

	@Schema(description = "공지 알림 활성화 여부", example = "true")
	boolean noticeEnabled,

	@Schema(description = "집중 타이머 알림 활성화 여부", example = "true")
	boolean homeEnabled,

	@Schema(description = "집중 타이머 알림 활성화 여부", example = "true")
	boolean concentrationEnabled,

	@Schema(description = "일기 알림 활성화 여부", example = "true")
	boolean diaryEnabled,

	@Schema(description = "소셜 알림 활성화 여부", example = "true")
	boolean socialEnabled
) {
}
