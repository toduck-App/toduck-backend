package im.toduck.domain.social.persistence.entity;

import java.time.LocalDateTime;

import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.base.entity.BaseEntity;
import jakarta.persistence.Entity;
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
@Getter
@Table(name = "comment_likes")
@NoArgsConstructor
public class CommentLike extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "comment_id", nullable = false)
	private Comment comment;

	@Builder
	private CommentLike(User user, Comment comment) {
		this.user = user;
		this.comment = comment;
	}

	public boolean isOwner(final User requestingUser) {
		return this.user.getId().equals(requestingUser.getId());
	}

	public boolean isForComment(final Comment comment) {
		return this.comment.getId().equals(comment.getId());
	}

	public void softDelete() {
		this.deletedAt = LocalDateTime.now();
	}
}
