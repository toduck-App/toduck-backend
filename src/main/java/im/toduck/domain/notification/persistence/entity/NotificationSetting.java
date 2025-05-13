package im.toduck.domain.notification.persistence.entity;

import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification_setting")
@Getter
@NoArgsConstructor
public class NotificationSetting extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "all_enabled", nullable = false)
	private boolean allEnabled;

	@Enumerated(EnumType.STRING)
	@Column(name = "notification_method", nullable = false)
	private NotificationMethod notificationMethod;

	@Column(name = "notice_enabled", nullable = false)
	private boolean noticeEnabled;

	@Column(name = "home_enabled", nullable = false)
	private boolean homeEnabled;

	@Column(name = "concentration_enabled", nullable = false)
	private boolean concentrationEnabled;

	@Column(name = "diary_enabled", nullable = false)
	private boolean diaryEnabled;

	@Column(name = "social_enabled", nullable = false)
	private boolean socialEnabled;

	@Builder
	private NotificationSetting(User user) {
		this.user = user;
		this.allEnabled = true;
		this.notificationMethod = NotificationMethod.SOUND_ONLY;
		this.noticeEnabled = true;
		this.homeEnabled = true;
		this.concentrationEnabled = true;
		this.diaryEnabled = true;
		this.socialEnabled = true;
	}

	public void updateAllEnabled(boolean enabled) {
		this.allEnabled = enabled;
	}

	public void updateNotificationMethod(NotificationMethod method) {
		this.notificationMethod = method;
	}

	public void updateNoticeEnabled(boolean enabled) {
		this.noticeEnabled = enabled;
	}

	public void updateHomeEnabled(boolean enabled) {
		this.homeEnabled = enabled;
	}

	public void updateConcentrationEnabled(boolean enabled) {
		this.concentrationEnabled = enabled;
	}

	public void updateDiaryEnabled(boolean enabled) {
		this.diaryEnabled = enabled;
	}

	public void updateSocialEnabled(boolean enabled) {
		this.socialEnabled = enabled;
	}

	public boolean isTypeEnabled(NotificationType type) {
		if (!allEnabled) {
			return false;
		}

		return switch (type.getCategory()) {
			case NOTICE -> noticeEnabled;
			case HOME -> homeEnabled;
			case CONCENTRATION -> concentrationEnabled;
			case DIARY -> diaryEnabled;
			case SOCIAL -> socialEnabled;
		};
	}
}
