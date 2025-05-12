package im.toduck.domain.notification.persistence.entity;

import java.util.Arrays;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;

import im.toduck.domain.notification.domain.data.NotificationData;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
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

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "data", columnDefinition = "json")
	@Getter(AccessLevel.NONE)
	private NotificationData data;

	@Column(nullable = false)
	private Boolean isRead;

	@Column(nullable = false)
	private Boolean isInAppShown;

	@Column(nullable = false)
	private Boolean isSent;

	private static final ObjectMapper objectMapper;

	static {
		objectMapper = new ObjectMapper();

		Arrays.stream(NotificationType.values()).forEach(type -> {
			Class<? extends NotificationData> dataClass = type.getDataClass();
			if (dataClass != null) {
				objectMapper.registerSubtypes(new NamedType(dataClass, type.name()));
			}
		});

		objectMapper.activateDefaultTyping(
			objectMapper.getPolymorphicTypeValidator(),
			ObjectMapper.DefaultTyping.NON_FINAL,
			JsonTypeInfo.As.PROPERTY
		);
	}

	@Builder
	private Notification(
		final User user,
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

	@SuppressWarnings("unchecked")
	public <T extends NotificationData> T getTypedData() {
		if (data == null) {
			return null;
		}

		Class<? extends NotificationData> dataClass = type.getDataClass();
		if (dataClass.isInstance(data)) {
			return (T)data;
		}

		throw new ClassCastException(
			"NotificationType " + type + "에 맞는 데이터 타입으로 변환할 수 없습니다. 실제 타입: " + data.getClass().getName()
		);
	}
}
