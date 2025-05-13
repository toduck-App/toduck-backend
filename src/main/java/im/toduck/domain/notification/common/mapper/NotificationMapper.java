package im.toduck.domain.notification.common.mapper;

import java.util.List;
import java.util.stream.Collectors;

import im.toduck.domain.notification.domain.data.NotificationData;
import im.toduck.domain.notification.domain.event.NotificationEvent;
import im.toduck.domain.notification.persistence.entity.Notification;
import im.toduck.domain.notification.persistence.entity.NotificationSetting;
import im.toduck.domain.notification.presentation.dto.response.NotificationDto;
import im.toduck.domain.notification.presentation.dto.response.NotificationListResponse;
import im.toduck.domain.notification.presentation.dto.response.NotificationSettingResponse;
import im.toduck.domain.user.persistence.entity.User;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class NotificationMapper {

	public static NotificationDto toNotificationDto(final Notification notification) {
		NotificationData data = notification.getData();

		return NotificationDto.builder()
			.id(notification.getId())
			.type(notification.getType())
			.title(notification.getInAppTitle())
			.body(notification.getInAppBody())
			.actionUrl(notification.getActionUrl())
			.data(data)
			.isRead(notification.getIsRead())
			.createdAt(notification.getCreatedAt())
			.build();
	}

	public static NotificationListResponse toNotificationListResponse(final List<Notification> notifications) {
		List<NotificationDto> notificationDtos = notifications.stream()
			.map(NotificationMapper::toNotificationDto)
			.collect(Collectors.toList());

		return NotificationListResponse.builder()
			.notifications(notificationDtos)
			.build();
	}

	public static NotificationSettingResponse toNotificationSettingResponse(final NotificationSetting settings) {
		return NotificationSettingResponse.builder()
			.allEnabled(settings.isAllEnabled())
			.notificationMethod(settings.getNotificationMethod())
			.noticeEnabled(settings.isNoticeEnabled())
			.homeEnabled(settings.isHomeEnabled())
			.concentrationEnabled(settings.isConcentrationEnabled())
			.diaryEnabled(settings.isDiaryEnabled())
			.socialEnabled(settings.isSocialEnabled())
			.build();
	}

	public static <T extends NotificationData> Notification toNotification(
		final User user,
		final NotificationEvent<T> event
	) {
		boolean isInAppShown = event.getType().isDefaultInAppShown();

		return Notification.builder()
			.user(user)
			.type(event.getType())
			.inAppTitle(event.getInAppTitle())
			.inAppBody(event.getInAppBody())
			.pushTitle(event.getPushTitle())
			.pushBody(event.getPushBody())
			.actionUrl(event.getActionUrl())
			.isInAppShown(isInAppShown)
			.notificationData(event.getData())
			.build();
	}
}
