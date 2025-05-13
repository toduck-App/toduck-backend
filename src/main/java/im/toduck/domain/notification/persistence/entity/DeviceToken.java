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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "device_token")
@Getter
@NoArgsConstructor
public class DeviceToken extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false, length = 512)
	private String token;

	@Column(nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	private DeviceType deviceType;

	@Builder
	private DeviceToken(User user, String token, DeviceType deviceType) {
		this.user = user;
		this.token = token;
		this.deviceType = deviceType;
	}

	public void updateToken(String token) {
		this.token = token;
	}
}
