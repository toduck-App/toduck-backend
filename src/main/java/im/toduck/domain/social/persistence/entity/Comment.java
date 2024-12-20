package im.toduck.domain.social.persistence.entity;

import java.time.LocalDateTime;

import im.toduck.domain.social.persistence.vo.CommentContent;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.global.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
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
@Getter
@Table(name = "comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Embedded
	private CommentContent content;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "social_id", nullable = false)
	private Social social;

	@Column(nullable = false, columnDefinition = "int default 0")
	private int likeCount;

	@Builder
	private Comment(User user, Social social, CommentContent content) {
		this.user = user;
		this.social = social;
		this.content = content;
	}

	public boolean isOwner(User requestingUser) {
		return this.user.getId().equals(requestingUser.getId());
	}

	public boolean isInSocialBoard(Social socialBoard) {
		return this.social.getId().equals(socialBoard.getId());
	}

	public void softDelete() {
		this.deletedAt = LocalDateTime.now();
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
