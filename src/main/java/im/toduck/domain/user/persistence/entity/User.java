package im.toduck.domain.user.persistence.entity;

import im.toduck.global.base.entity.BaseEntity;
import im.toduck.global.exception.CommonException;
import im.toduck.global.exception.ExceptionCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private UserRole role;

	@Column(nullable = false, length = 100)
	private String nickname;

	@Column(nullable = true, length = 50)
	private String phoneNumber;

	@Column(nullable = true, length = 100)
	private String loginId;

	@Column(nullable = true)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(nullable = true, length = 100)
	private OAuthProvider provider;

	@Column(nullable = true, length = 100)
	private String email;

	@Builder
	private User(UserRole role, String nickname, String phoneNumber, String loginId, String password,
		OAuthProvider provider, String email) {
		this.role = role;
		this.nickname = nickname;
		this.phoneNumber = phoneNumber;
		this.loginId = loginId;
		this.password = password;
		this.provider = provider;
		this.email = email;
		validateOAuthOrGeneralFields();
	}

	private void validateOAuthOrGeneralFields() {
		if (isOAuthUser()) {
			if (!(phoneNumber == null && loginId == null && password == null)) {
				throw CommonException.from(ExceptionCode.INVALID_USER_FILED);
			}
		} else if (isGeneralUser()) {
			if (!(provider == null && email == null)) {
				throw CommonException.from(ExceptionCode.INVALID_USER_FILED);
			}
		} else {
			throw CommonException.from(ExceptionCode.INVALID_USER_FILED);
		}
	}

	private boolean isGeneralUser() {
		return phoneNumber != null && loginId != null && password != null;
	}

	private boolean isOAuthUser() {
		return this.provider != null && this.email != null;
	}
}
