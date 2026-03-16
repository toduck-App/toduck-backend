package im.toduck.domain.badge.persistence.entity;

import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "user_badge")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserBadge extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "badge_id", nullable = false)
	private Badge badge;

	@Column(name = "is_representative", nullable = false)
	private boolean isRepresentative;

	@Column(name = "is_seen", nullable = false)
	private boolean isSeen;

	@Builder
	private UserBadge(User user, Badge badge) {
		this.user = user;
		this.badge = badge;
		this.isRepresentative = false;
		this.isSeen = false;
	}

	public void markAsSeen() {
		this.isSeen = true;
	}

	public void updateRepresentativeStatus(boolean isRepresentative) {
		this.isRepresentative = isRepresentative;
	}
}
