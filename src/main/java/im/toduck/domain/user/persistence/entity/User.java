package im.toduck.domain.user.persistence.entity;

import im.toduck.global.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

	private User(String nickname, String loginId, String password, String phoneNumber) {
		this.role = UserRole.USER;
		this.nickname = nickname;
		this.loginId = loginId;
		this.password = password;
		this.phoneNumber = phoneNumber;
	}

	private User(String nickname, OAuthProvider provider, String email) {
		this.role = UserRole.USER;
		this.nickname = nickname;
		this.provider = provider;
		this.email = email;
	}

	public static User createGeneralUser(String nickname, String loginId, String password, String phoneNumber) {
		return new User(nickname, loginId, password, phoneNumber);
	}

	public static User createOAuthUser(String nickname, OAuthProvider provider,
		String email) { // TODO : 팀원 pr merge 후 mapper 로 변경
		return new User(nickname, provider, email);
	}
}
