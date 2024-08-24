package im.toduck.domain.social.persistence.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "social")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE social SET deleted_at = NOW() where id=?")
@SQLRestriction(value = "deleted_at is NULL")
public class Social extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	// TODO: Routine 추가 (생성자, 정적 팩터리 메소드에도 추가 필요)

	@Column(nullable = false, length = 255)
	private String content;

	@Column(nullable = false)
	private Boolean isAnonymous;

	private Social(User user, String content, Boolean isAnonymous) {
		this.user = user;
		this.content = content;
		this.isAnonymous = isAnonymous;
	}

	public static Social of(User user, String content, Boolean isAnonymous) {
		return new Social(user, content, isAnonymous);
	}

	public boolean isOwner(User requestingUser) {
		return this.user.getId().equals(requestingUser.getId());
	}

	public void updateContent(String content) {
		this.content = content;
	}

	public void updateIsAnonymous(Boolean isAnonymous) {
		this.isAnonymous = isAnonymous;
	}
}
