package im.toduck.domain.social.persistence.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import im.toduck.domain.routine.persistence.entity.Routine;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "routine_id")
	private Routine routine;

	@Column(nullable = true, length = 100)
	private String title;

	@Column(nullable = false, columnDefinition = "int default 0")
	private int likeCount;

	@Column(nullable = false, length = 500)
	private String content;

	@Column(nullable = false)
	private Boolean isAnonymous;

	@Builder
	private Social(User user, Routine routine, String title, String content, Boolean isAnonymous) {
		this.user = user;
		this.routine = routine;
		this.title = title;
		this.content = content;
		this.isAnonymous = isAnonymous;
	}

	public boolean isOwner(User requestingUser) {
		return this.user.getId().equals(requestingUser.getId());
	}

	public void updateTitle(String title) {
		this.title = title;
	}

	public void updateContent(String content) {
		this.content = content;
	}

	public void updateIsAnonymous(Boolean isAnonymous) {
		this.isAnonymous = isAnonymous;
	}

	public void updateRoutine(final Routine routine) {
		this.routine = routine;
	}

	public void incrementLikeCount() {
		this.likeCount++;
	}

	public void decrementLikeCount() {
		if (this.likeCount > 0) {
			this.likeCount--;
		}
	}

}
