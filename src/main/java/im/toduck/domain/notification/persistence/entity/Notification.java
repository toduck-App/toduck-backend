package im.toduck.domain.notification.persistence.entity;

import im.toduck.domain.notification.common.converter.NotificationDataConverter;
import im.toduck.domain.notification.domain.data.NotificationData;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification")
@Getter
@NoArgsConstructor
public class Notification extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_id", nullable = true)
	private User sender;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private NotificationType type;

	@Column(name = "in_app_title", nullable = false, length = 100)
	private String inAppTitle;

	@Column(name = "in_app_body", nullable = false, length = 500)
	private String inAppBody;

	@Column(name = "push_title", nullable = false, length = 100)
	private String pushTitle;

	@Column(name = "push_body", nullable = false, length = 500)
	private String pushBody;

	@Column(length = 1024)
	private String actionUrl;

	@Convert(converter = NotificationDataConverter.class)
	@Column(name = "data", columnDefinition = "json")
	private NotificationData data;

	@Column(nullable = false)
	private Boolean isRead;

	@Column(nullable = false)
	private Boolean isInAppShown;

	@Column(nullable = false)
	private Boolean isSent;

	@Builder
	private Notification(
		final User user,
		final User sender,
		final NotificationType type,
		final String inAppTitle,
		final String inAppBody,
		final String pushTitle,
		final String pushBody,
		final String actionUrl,
		final Boolean isInAppShown,
		final NotificationData notificationData
	) {
		this.user = user;
		this.sender = sender;
		this.type = type;
		this.inAppTitle = inAppTitle;
		this.inAppBody = inAppBody;
		this.pushTitle = pushTitle;
		this.pushBody = pushBody;
		this.actionUrl = actionUrl;
		this.isRead = false;
		this.isInAppShown = isInAppShown;
		this.isSent = false;
		this.data = notificationData;
	}

	public void markAsRead() {
		this.isRead = true;
	}

	public void markAsSent() {
		this.isSent = true;
	}

	public Long getSenderId() {
		if (sender == null) {
			return null;
		}

		return sender.getId();
	}

	public String getSenderProfileImageUrl() {
		if (sender == null) {
			return null;
		}

		return sender.getImageUrl();
	}
}
