package im.toduck.domain.admin.persistence.entity;

import org.hibernate.annotations.SQLDelete;

import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "admin")
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE admin SET deleted_at = NOW() where id=?")
public class Admin extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@Column(length = 255, nullable = false)
	private String displayName;

	@Builder
	private Admin(User user,
		String displayName) {
		this.user = user;
		this.displayName = displayName;
	}

	public void updateDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	public void revive() {
		this.deletedAt = null;
	}
}
