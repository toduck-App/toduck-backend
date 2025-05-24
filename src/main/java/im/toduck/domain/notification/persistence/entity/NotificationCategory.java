package im.toduck.domain.notification.persistence.entity;

import lombok.Getter;

@Getter
public enum NotificationCategory {
	NOTICE("공지 알림"),
	HOME("일정, 루틴 알림"),
	CONCENTRATION("집중 알림"),
	DIARY("일기 알림"),
	SOCIAL("소셜 알림");

	private final String description;

	NotificationCategory(String description) {
		this.description = description;
	}
}
